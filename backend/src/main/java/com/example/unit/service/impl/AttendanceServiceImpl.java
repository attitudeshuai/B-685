package com.example.unit.service.impl;

import com.example.unit.dto.AttendanceRecordDTO;
import com.example.unit.dto.AttendanceSummaryDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.AttendanceRecord;
import com.example.unit.entity.SysUser;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.AttendanceRecordRepository;
import com.example.unit.repository.UserRepository;
import com.example.unit.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);

    private final AttendanceRecordRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceServiceImpl(AttendanceRecordRepository attendanceRepository,
                                 UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AttendanceRecord checkIn(Long userId) {
        logger.info("上班打卡: userId={}", userId);
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existing = attendanceRepository.findByUserIdAndRecordDate(userId, today);
        
        AttendanceRecord record;
        if (existing.isPresent()) {
            record = existing.get();
            if (record.getCheckInTime() != null) {
                throw new BusinessException(400, "今日已打过上班卡");
            }
        } else {
            record = new AttendanceRecord();
            record.setUserId(userId);
            record.setRecordDate(today);
        }

        LocalDateTime now = LocalDateTime.now();
        record.setCheckInTime(now);
        updateStatus(record);
        return attendanceRepository.save(record);
    }

    @Override
    public AttendanceRecord checkOut(Long userId) {
        logger.info("下班打卡: userId={}", userId);
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRepository.findByUserIdAndRecordDate(userId, today)
                .orElseThrow(() -> new BusinessException(400, "今日未打上班卡，请先打上班卡"));
        
        if (record.getCheckOutTime() != null) {
            throw new BusinessException(400, "今日已打过下班卡");
        }

        record.setCheckOutTime(LocalDateTime.now());
        updateStatus(record);
        return attendanceRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceRecordDTO getTodayRecord(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> record = attendanceRepository.findByUserIdAndRecordDate(userId, today);
        return record.map(this::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordDTO> getMyRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        List<AttendanceRecord> records = attendanceRepository.findByUserIdAndRecordDateBetweenOrderByRecordDateDesc(userId, start, end);
        return records.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AttendanceRecordDTO> getAllRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<AttendanceRecord> result = attendanceRepository.findByConditions(userId, startDate, endDate, PageRequest.of(page - 1, size));
        List<AttendanceRecordDTO> dtos = result.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return PageResult.of(dtos, result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSummaryDTO> getMonthlySummary(Integer year, Integer month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<AttendanceRecord> allRecords = attendanceRepository
                .findByRecordDateBetweenOrderByRecordDateDesc(startDate, endDate);

        Map<Long, List<AttendanceRecord>> recordsByUser = allRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getUserId));

        Set<Long> userIds = recordsByUser.keySet();
        List<SysUser> users = userIds.isEmpty() ? Collections.emptyList() : userRepository.findAllById(userIds);
        Map<Long, SysUser> userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        List<AttendanceSummaryDTO> summaries = new ArrayList<>();
        for (Map.Entry<Long, List<AttendanceRecord>> entry : recordsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<AttendanceRecord> records = entry.getValue();
            SysUser user = userMap.get(userId);

            AttendanceSummaryDTO summary = new AttendanceSummaryDTO();
            summary.setUserId(userId);
            if (user != null) {
                summary.setUsername(user.getUsername());
                summary.setNickname(user.getNickname());
            }
            summary.setYear(year);
            summary.setMonth(month);

            int normal = 0, late = 0, early = 0, absent = 0;
            for (AttendanceRecord record : records) {
                boolean hasLate = isLateCheckIn(record);
                boolean hasEarly = isEarlyCheckOut(record);

                if (record.getCheckInTime() == null) {
                    absent++;
                } else if (hasLate || hasEarly) {
                    if (hasLate) late++;
                    if (hasEarly) early++;
                } else {
                    normal++;
                }
            }

            int total = records.size();
            summary.setTotalDays(total);
            summary.setNormalDays(normal);
            summary.setLateDays(late);
            summary.setEarlyDays(early);
            summary.setAbsentDays(absent);
            summary.setAttendanceRate(total > 0 ? (double) normal / total * 100 : 0.0);

            summaries.add(summary);
        }

        return summaries;
    }

    private boolean isLateCheckIn(AttendanceRecord record) {
        return record.getCheckInTime() != null
                && record.getCheckInTime().toLocalTime().isAfter(WORK_START_TIME);
    }

    private boolean isEarlyCheckOut(AttendanceRecord record) {
        return record.getCheckOutTime() != null
                && record.getCheckOutTime().toLocalTime().isBefore(WORK_END_TIME);
    }

    private void updateStatus(AttendanceRecord record) {
        if (record.getCheckInTime() == null) {
            record.setStatus(4);
            return;
        }

        boolean isLate = isLateCheckIn(record);

        if (record.getCheckOutTime() == null) {
            record.setStatus(isLate ? 2 : 1);
            return;
        }

        boolean isEarly = isEarlyCheckOut(record);

        if (isLate && isEarly) {
            record.setStatus(5);
        } else if (isLate) {
            record.setStatus(2);
        } else if (isEarly) {
            record.setStatus(3);
        } else {
            record.setStatus(1);
        }
    }

    private AttendanceRecordDTO toDTO(AttendanceRecord record) {
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setCheckInTime(record.getCheckInTime());
        dto.setCheckOutTime(record.getCheckOutTime());
        dto.setRecordDate(record.getRecordDate());
        dto.setStatus(record.getStatus());
        dto.setRemark(record.getRemark());
        dto.setCreateTime(record.getCreateTime());
        dto.setUpdateTime(record.getUpdateTime());

        switch (record.getStatus()) {
            case 1: dto.setStatusText("正常"); break;
            case 2: dto.setStatusText("迟到"); break;
            case 3: dto.setStatusText("早退"); break;
            case 4: dto.setStatusText("缺勤"); break;
            case 5: dto.setStatusText("迟到+早退"); break;
            default: dto.setStatusText("未知");
        }

        userRepository.findById(record.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
        });

        return dto;
    }
}
