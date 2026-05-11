package com.example.unit.service.impl;

import com.example.unit.dto.AttendanceDTO;
import com.example.unit.dto.AttendanceMonthlySummaryDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.Attendance;
import com.example.unit.entity.SysUser;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.AttendanceRepository;
import com.example.unit.repository.UserRepository;
import com.example.unit.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(18, 0);

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AttendanceDTO checkIn(Long userId, String location, String ip) {
        logger.info("用户打卡上班: userId={}", userId);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElse(new Attendance());

        if (attendance.getCheckInTime() != null) {
            throw new BusinessException(400, "今日已打卡上班");
        }

        attendance.setUserId(userId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setCheckInIp(ip);
        attendance.setCheckInLocation(location);

        if (LocalTime.now().isAfter(STANDARD_CHECK_IN_TIME)) {
            attendance.setStatus("LATE");
        } else {
            attendance.setStatus("NORMAL");
        }

        Attendance saved = attendanceRepository.save(attendance);
        return toDTO(saved);
    }

    @Override
    public AttendanceDTO checkOut(Long userId, String location, String ip) {
        logger.info("用户打卡下班: userId={}", userId);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new BusinessException(400, "今日未打卡上班"));

        if (attendance.getCheckOutTime() != null) {
            throw new BusinessException(400, "今日已打卡下班");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setCheckOutIp(ip);
        attendance.setCheckOutLocation(location);

        if (attendance.getCheckInTime() != null) {
            Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            attendance.setWorkHours(duration.toHours() + duration.toMinutesPart() / 60.0);
        }

        if (LocalTime.now().isBefore(STANDARD_CHECK_OUT_TIME) && "NORMAL".equals(attendance.getStatus())) {
            attendance.setStatus("EARLY_LEAVE");
        } else if (LocalTime.now().isBefore(STANDARD_CHECK_OUT_TIME) && "LATE".equals(attendance.getStatus())) {
            attendance.setStatus("LATE_AND_EARLY");
        }

        Attendance saved = attendanceRepository.save(attendance);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getMyAttendance(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> list = attendanceRepository.findByUserIdAndAttendanceDateBetween(userId, startDate, endDate);
        Map<Long, SysUser> userMap = getUserMap(List.of(userId));
        return list.stream().map(a -> toDTO(a, userMap)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AttendanceDTO> findByPage(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Page<Attendance> result = attendanceRepository.findByConditions(userId, startDate, endDate, PageRequest.of(page - 1, size));
        
        List<Long> userIds = result.getContent().stream().map(Attendance::getUserId).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = getUserMap(userIds);
        
        List<AttendanceDTO> dtos = result.getContent().stream().map(a -> toDTO(a, userMap)).collect(Collectors.toList());
        return PageResult.of(dtos, result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceMonthlySummaryDTO getMonthlySummary(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        AttendanceMonthlySummaryDTO summary = new AttendanceMonthlySummaryDTO();
        summary.setUserId(userId);
        summary.setYear(year);
        summary.setMonth(month);

        SysUser user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            summary.setUsername(user.getUsername());
            summary.setNickname(user.getNickname());
        }

        summary.setTotalWorkDays((long) yearMonth.lengthOfMonth());
        summary.setCheckInDays(attendanceRepository.countCheckInDays(userId, startDate, endDate));
        summary.setCheckOutDays(attendanceRepository.countCheckOutDays(userId, startDate, endDate));
        summary.setLateDays(attendanceRepository.countLateDays(userId, startDate, endDate));
        summary.setEarlyLeaveDays(attendanceRepository.countEarlyLeaveDays(userId, startDate, endDate));
        summary.setAbsentDays(summary.getTotalWorkDays() - summary.getCheckInDays());
        summary.setTotalWorkHours(attendanceRepository.sumWorkHours(userId, startDate, endDate));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceMonthlySummaryDTO> getMonthlySummaryList(Long unitId, Long deptId, Integer year, Integer month) {
        List<SysUser> users = userRepository.findAll();
        List<AttendanceMonthlySummaryDTO> result = new ArrayList<>();
        
        for (SysUser user : users) {
            if (unitId != null && !unitId.equals(user.getUnitId())) continue;
            if (deptId != null && !deptId.equals(user.getDeptId())) continue;
            
            AttendanceMonthlySummaryDTO summary = getMonthlySummary(user.getId(), year, month);
            result.add(summary);
        }
        
        return result;
    }

    private Map<Long, SysUser> getUserMap(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
    }

    private AttendanceDTO toDTO(Attendance attendance) {
        return toDTO(attendance, null);
    }

    private AttendanceDTO toDTO(Attendance attendance, Map<Long, SysUser> userMap) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setUserId(attendance.getUserId());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setCheckInIp(attendance.getCheckInIp());
        dto.setCheckOutIp(attendance.getCheckOutIp());
        dto.setCheckInLocation(attendance.getCheckInLocation());
        dto.setCheckOutLocation(attendance.getCheckOutLocation());
        dto.setStatus(attendance.getStatus());
        dto.setWorkHours(attendance.getWorkHours());
        dto.setRemark(attendance.getRemark());

        if (userMap != null && userMap.containsKey(attendance.getUserId())) {
            SysUser user = userMap.get(attendance.getUserId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
        }

        return dto;
    }
}
