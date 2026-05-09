package com.example.unit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDTO {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private LocalDate attendanceDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String clockInStatus;
    private String clockOutStatus;
    private String remark;

    public AttendanceDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }
    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }
    public String getClockInStatus() { return clockInStatus; }
    public void setClockInStatus(String clockInStatus) { this.clockInStatus = clockInStatus; }
    public String getClockOutStatus() { return clockOutStatus; }
    public void setClockOutStatus(String clockOutStatus) { this.clockOutStatus = clockOutStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
