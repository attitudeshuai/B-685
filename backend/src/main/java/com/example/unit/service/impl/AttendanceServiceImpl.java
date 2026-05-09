package com.example.unit.service.impl;

import com.example.unit.dto.AttendanceDTO;
import com.example.unit.dto.AttendanceSummaryDTO;
import com.example.unit.entity.AttendanceRecord;
import com.example.unit.entity.SysUser;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.AttendanceRepository;
import com.example.unit.repository.DepartmentRepository;
import com.example.unit.repository.UserRepository;
import com.example.unit.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private static final LocalTime CLOCK_IN_DEADLINE = LocalTime.of(9, 0);
    private static final LocalTime CLOCK_OUT_STANDARD = LocalTime.of(18, 0);

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                  UserRepository userRepository,
                                  DepartmentRepository departmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public AttendanceDTO clockIn(Long userId) {
        logger.info("上班打卡: userId={}", userId);
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord record = attendanceRepository
                .findByUserIdAndAttendanceDate(userId, today)
                .orElse(null);

        if (record != null && record.getClockInTime() != null) {
            throw new BusinessException(400, "今天已打过上班卡");
        }

        if (record == null) {
            record = new AttendanceRecord();
            record.setUserId(userId);
            record.setAttendanceDate(today);
        }

        record.setClockInTime(now);
        record.setClockInStatus(now.toLocalTime().isAfter(CLOCK_IN_DEADLINE) ? "迟到" : "正常");

        AttendanceRecord saved = attendanceRepository.save(record);
        return toDTO(saved, user);
    }

    @Override
    public AttendanceDTO clockOut(Long userId) {
        logger.info("下班打卡: userId={}", userId);
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        AttendanceRecord record = attendanceRepository
                .findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new BusinessException(400, "请先打上班卡"));

        if (record.getClockOutTime() != null) {
            throw new BusinessException(400, "今天已打过下班卡");
        }

        record.setClockOutTime(now);
        record.setClockOutStatus(now.toLocalTime().isBefore(CLOCK_OUT_STANDARD) ? "早退" : "正常");

        AttendanceRecord saved = attendanceRepository.save(record);
        return toDTO(saved, user);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getTodayRecord(Long userId) {
        LocalDate today = LocalDate.now();
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .map(record -> toDTO(record, user))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getMonthlyRecords(Long userId, Integer year, Integer month) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        LocalDate now = LocalDate.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();
        YearMonth ym = YearMonth.of(y, m);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<AttendanceRecord> records = attendanceRepository
                .findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(userId, startDate, endDate);

        return records.stream()
                .map(record -> toDTO(record, user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getRecordsByCondition(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = attendanceRepository.findByConditions(userId, startDate, endDate);

        Set<Long> userIds = records.stream().map(AttendanceRecord::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        return records.stream()
                .map(record -> toDTO(record, userMap.get(record.getUserId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSummaryDTO> getMonthlySummary(Integer year, Integer month, Long userId, Long deptId) {
        LocalDate now = LocalDate.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();
        YearMonth ym = YearMonth.of(y, m);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        int workingDaysInMonth = countWorkingDays(startDate, endDate);

        List<SysUser> users;
        if (userId != null) {
            users = userRepository.findById(userId).map(Collections::singletonList).orElse(Collections.emptyList());
        } else if (deptId != null) {
            users = userRepository.findByConditions(null, null, null, deptId, 1,
                            org.springframework.data.domain.Pageable.unpaged())
                    .getContent();
        } else {
            users = userRepository.findByConditions(null, null, null, null, 1,
                            org.springframework.data.domain.Pageable.unpaged())
                    .getContent();
        }

        List<AttendanceSummaryDTO> summaries = new ArrayList<>();
        for (SysUser user : users) {
            List<AttendanceRecord> records = attendanceRepository
                    .findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(user.getId(), startDate, endDate);

            AttendanceSummaryDTO summary = new AttendanceSummaryDTO();
            summary.setUserId(user.getId());
            summary.setUsername(user.getUsername());
            summary.setNickname(user.getNickname());
            summary.setTotalDays(workingDaysInMonth);
            summary.setNormalDays(0);
            summary.setLateDays(0);
            summary.setEarlyDays(0);
            summary.setAbsentDays(0);

            if (user.getDeptId() != null) {
                departmentRepository.findById(user.getDeptId())
                        .ifPresent(dept -> summary.setDeptName(dept.getName()));
            }

            int attendedDays = 0;
            for (AttendanceRecord r : records) {
                boolean isLate = "迟到".equals(r.getClockInStatus());
                boolean isEarly = "早退".equals(r.getClockOutStatus());
                if (isLate) summary.setLateDays(summary.getLateDays() + 1);
                if (isEarly) summary.setEarlyDays(summary.getEarlyDays() + 1);
                if (!isLate && !isEarly) summary.setNormalDays(summary.getNormalDays() + 1);
                attendedDays++;
            }
            summary.setAbsentDays(Math.max(0, workingDaysInMonth - attendedDays));
            summaries.add(summary);
        }

        return summaries;
    }

    private int countWorkingDays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            java.time.DayOfWeek dow = d.getDayOfWeek();
            if (dow != java.time.DayOfWeek.SATURDAY && dow != java.time.DayOfWeek.SUNDAY) {
                count++;
            }
            d = d.plusDays(1);
        }
        return count;
    }

    private AttendanceDTO toDTO(AttendanceRecord record, SysUser user) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setAttendanceDate(record.getAttendanceDate());
        dto.setClockInTime(record.getClockInTime());
        dto.setClockOutTime(record.getClockOutTime());
        dto.setClockInStatus(record.getClockInStatus());
        dto.setClockOutStatus(record.getClockOutStatus());
        dto.setRemark(record.getRemark());
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
        }
        return dto;
    }
}
