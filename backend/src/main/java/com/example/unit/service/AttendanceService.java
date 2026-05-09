package com.example.unit.service;

import com.example.unit.dto.AttendanceDTO;
import com.example.unit.dto.AttendanceSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO clockIn(Long userId);

    AttendanceDTO clockOut(Long userId);

    AttendanceDTO getTodayRecord(Long userId);

    List<AttendanceDTO> getMonthlyRecords(Long userId, Integer year, Integer month);

    List<AttendanceDTO> getRecordsByCondition(Long userId, LocalDate startDate, LocalDate endDate);

    List<AttendanceSummaryDTO> getMonthlySummary(Integer year, Integer month, Long userId, Long deptId);
}
