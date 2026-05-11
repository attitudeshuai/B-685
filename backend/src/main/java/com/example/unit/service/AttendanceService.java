package com.example.unit.service;

import com.example.unit.dto.AttendanceDTO;
import com.example.unit.dto.AttendanceMonthlySummaryDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO checkIn(Long userId, String location, String ip);

    AttendanceDTO checkOut(Long userId, String location, String ip);

    AttendanceDTO getTodayAttendance(Long userId);

    List<AttendanceDTO> getMyAttendance(Long userId, LocalDate startDate, LocalDate endDate);

    PageResult<AttendanceDTO> findByPage(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    AttendanceMonthlySummaryDTO getMonthlySummary(Long userId, Integer year, Integer month);

    List<AttendanceMonthlySummaryDTO> getMonthlySummaryList(Long unitId, Long deptId, Integer year, Integer month);
}
