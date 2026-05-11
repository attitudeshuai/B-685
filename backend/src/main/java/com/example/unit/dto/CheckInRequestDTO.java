package com.example.unit.dto;

public class CheckInRequestDTO {

    private Long userId;
    private String location;
    private String ip;

    public CheckInRequestDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
