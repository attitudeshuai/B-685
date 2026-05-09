package com.example.unit.service;

import com.example.unit.dto.AttendanceRecordDTO;
import com.example.unit.dto.AttendanceSummaryDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceRecord checkIn(Long userId);

    AttendanceRecord checkOut(Long userId);

    AttendanceRecordDTO getTodayRecord(Long userId);

    List<AttendanceRecordDTO> getMyRecords(Long userId, LocalDate startDate, LocalDate endDate);

    PageResult<AttendanceRecordDTO> getAllRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    List<AttendanceSummaryDTO> getMonthlySummary(Integer year, Integer month);
}
