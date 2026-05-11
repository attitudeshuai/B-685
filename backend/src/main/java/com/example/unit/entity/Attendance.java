package com.example.unit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", indexes = {
    @Index(name = "idx_attendance_user_id", columnList = "user_id"),
    @Index(name = "idx_attendance_date", columnList = "attendance_date"),
    @Index(name = "idx_attendance_user_date", columnList = "user_id, attendance_date")
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_in_ip", length = 50)
    private String checkInIp;

    @Column(name = "check_out_ip", length = 50)
    private String checkOutIp;

    @Column(name = "check_in_location", length = 200)
    private String checkInLocation;

    @Column(name = "check_out_location", length = 200)
    private String checkOutLocation;

    @Column(name = "status", length = 20)
    private String status = "NORMAL";

    @Column(name = "work_hours")
    private Double workHours;

    @Column(name = "remark", length = 500)
    private String remark;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    public String getCheckInIp() { return checkInIp; }
    public void setCheckInIp(String checkInIp) { this.checkInIp = checkInIp; }
    public String getCheckOutIp() { return checkOutIp; }
    public void setCheckOutIp(String checkOutIp) { this.checkOutIp = checkOutIp; }
    public String getCheckInLocation() { return checkInLocation; }
    public void setCheckInLocation(String checkInLocation) { this.checkInLocation = checkInLocation; }
    public String getCheckOutLocation() { return checkOutLocation; }
    public void setCheckOutLocation(String checkOutLocation) { this.checkOutLocation = checkOutLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getWorkHours() { return workHours; }
    public void setWorkHours(Double workHours) { this.workHours = workHours; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
