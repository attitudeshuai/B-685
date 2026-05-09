package com.example.unit.dto;

public class AttendanceSummaryDTO {

    private Long userId;
    private String username;
    private String nickname;
    private String deptName;
    private Integer totalDays;
    private Integer normalDays;
    private Integer lateDays;
    private Integer earlyDays;
    private Integer absentDays;

    public AttendanceSummaryDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    public Integer getNormalDays() { return normalDays; }
    public void setNormalDays(Integer normalDays) { this.normalDays = normalDays; }
    public Integer getLateDays() { return lateDays; }
    public void setLateDays(Integer lateDays) { this.lateDays = lateDays; }
    public Integer getEarlyDays() { return earlyDays; }
    public void setEarlyDays(Integer earlyDays) { this.earlyDays = earlyDays; }
    public Integer getAbsentDays() { return absentDays; }
    public void setAbsentDays(Integer absentDays) { this.absentDays = absentDays; }
}
