package com.example.unit.controller;

import com.example.unit.dto.ApiResponse;
import com.example.unit.dto.AttendanceDTO;
import com.example.unit.dto.AttendanceSummaryDTO;
import com.example.unit.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    public ApiResponse<AttendanceDTO> clockIn(@RequestParam Long userId) {
        logger.info("上班打卡: userId={}", userId);
        return ApiResponse.success("上班打卡成功", attendanceService.clockIn(userId));
    }

    @PostMapping("/clock-out")
    public ApiResponse<AttendanceDTO> clockOut(@RequestParam Long userId) {
        logger.info("下班打卡: userId={}", userId);
        return ApiResponse.success("下班打卡成功", attendanceService.clockOut(userId));
    }

    @GetMapping("/today")
    public ApiResponse<AttendanceDTO> getTodayRecord(@RequestParam Long userId) {
        return ApiResponse.success(attendanceService.getTodayRecord(userId));
    }

    @GetMapping("/monthly")
    public ApiResponse<List<AttendanceDTO>> getMonthlyRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ApiResponse.success(attendanceService.getMonthlyRecords(userId, year, month));
    }

    @GetMapping("/records")
    public ApiResponse<List<AttendanceDTO>> getRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        return ApiResponse.success(attendanceService.getRecordsByCondition(userId, start, end));
    }

    @GetMapping("/summary")
    public ApiResponse<List<AttendanceSummaryDTO>> getMonthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long deptId) {
        return ApiResponse.success(attendanceService.getMonthlySummary(year, month, userId, deptId));
    }
}
