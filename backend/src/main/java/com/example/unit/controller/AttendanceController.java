package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponse<AttendanceDTO> checkIn(@RequestBody CheckInRequestDTO request,
                                               HttpServletRequest httpRequest) {
        logger.info("用户打卡上班: userId={}", request.getUserId());
        String location = request.getLocation();
        String ip = request.getIp() != null ? request.getIp() : getClientIp(httpRequest);
        return ApiResponse.success("打卡成功", attendanceService.checkIn(request.getUserId(), location, ip));
    }

    @PostMapping("/check-out")
    public ApiResponse<AttendanceDTO> checkOut(@RequestBody CheckInRequestDTO request,
                                                HttpServletRequest httpRequest) {
        logger.info("用户打卡下班: userId={}", request.getUserId());
        String location = request.getLocation();
        String ip = request.getIp() != null ? request.getIp() : getClientIp(httpRequest);
        return ApiResponse.success("打卡成功", attendanceService.checkOut(request.getUserId(), location, ip));
    }

    @GetMapping("/today")
    public ApiResponse<AttendanceDTO> getTodayAttendance(@RequestParam Long userId) {
        return ApiResponse.success(attendanceService.getTodayAttendance(userId));
    }

    @GetMapping("/my")
    public ApiResponse<List<AttendanceDTO>> getMyAttendance(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(attendanceService.getMyAttendance(userId, startDate, endDate));
    }

    @GetMapping
    public ApiResponse<PageResult<AttendanceDTO>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询考勤记录: userId={}", userId);
        return ApiResponse.success(attendanceService.findByPage(userId, startDate, endDate, page, size));
    }

    @GetMapping("/summary/{userId}")
    public ApiResponse<AttendanceMonthlySummaryDTO> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ApiResponse.success(attendanceService.getMonthlySummary(userId, year, month));
    }

    @GetMapping("/summary")
    public ApiResponse<List<AttendanceMonthlySummaryDTO>> getMonthlySummaryList(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long deptId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        logger.info("查询月度考勤汇总: unitId={}, deptId={}, year={}, month={}", unitId, deptId, year, month);
        return ApiResponse.success(attendanceService.getMonthlySummaryList(unitId, deptId, year, month));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
