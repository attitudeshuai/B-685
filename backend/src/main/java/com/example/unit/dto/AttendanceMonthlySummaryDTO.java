package com.example.unit.dto;

public class AttendanceMonthlySummaryDTO {

    private Long userId;
    private String username;
    private String nickname;
    private Integer year;
    private Integer month;
    private Long totalWorkDays;
    private Long checkInDays;
    private Long checkOutDays;
    private Long lateDays;
    private Long earlyLeaveDays;
    private Long absentDays;
    private Double totalWorkHours;

    public AttendanceMonthlySummaryDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Long getTotalWorkDays() { return totalWorkDays; }
    public void setTotalWorkDays(Long totalWorkDays) { this.totalWorkDays = totalWorkDays; }
    public Long getCheckInDays() { return checkInDays; }
    public void setCheckInDays(Long checkInDays) { this.checkInDays = checkInDays; }
    public Long getCheckOutDays() { return checkOutDays; }
    public void setCheckOutDays(Long checkOutDays) { this.checkOutDays = checkOutDays; }
    public Long getLateDays() { return lateDays; }
    public void setLateDays(Long lateDays) { this.lateDays = lateDays; }
    public Long getEarlyLeaveDays() { return earlyLeaveDays; }
    public void setEarlyLeaveDays(Long earlyLeaveDays) { this.earlyLeaveDays = earlyLeaveDays; }
    public Long getAbsentDays() { return absentDays; }
    public void setAbsentDays(Long absentDays) { this.absentDays = absentDays; }
    public Double getTotalWorkHours() { return totalWorkHours; }
    public void setTotalWorkHours(Double totalWorkHours) { this.totalWorkHours = totalWorkHours; }
}
