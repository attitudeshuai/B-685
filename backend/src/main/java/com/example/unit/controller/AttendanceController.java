package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.entity.AttendanceRecord;
import com.example.unit.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ApiResponse<AttendanceRecord> checkIn(@RequestParam Long userId) {
        logger.info("上班打卡: userId={}", userId);
        AttendanceRecord record = attendanceService.checkIn(userId);
        return ApiResponse.success("上班打卡成功", record);
    }

    @PostMapping("/check-out")
    public ApiResponse<AttendanceRecord> checkOut(@RequestParam Long userId) {
        logger.info("下班打卡: userId={}", userId);
        AttendanceRecord record = attendanceService.checkOut(userId);
        return ApiResponse.success("下班打卡成功", record);
    }

    @GetMapping("/today")
    public ApiResponse<AttendanceRecordDTO> getTodayRecord(@RequestParam Long userId) {
        return ApiResponse.success(attendanceService.getTodayRecord(userId));
    }

    @GetMapping("/my-records")
    public ApiResponse<List<AttendanceRecordDTO>> getMyRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(attendanceService.getMyRecords(userId, startDate, endDate));
    }

    @GetMapping("/records")
    public ApiResponse<PageResult<AttendanceRecordDTO>> getAllRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询考勤记录: userId={}, startDate={}, endDate={}", userId, startDate, endDate);
        return ApiResponse.success(attendanceService.getAllRecords(userId, startDate, endDate, page, size));
    }

    @GetMapping("/monthly-summary")
    public ApiResponse<List<AttendanceSummaryDTO>> getMonthlySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        logger.info("查询月度汇总: year={}, month={}", year, month);
        return ApiResponse.success(attendanceService.getMonthlySummary(year, month));
    }
}
