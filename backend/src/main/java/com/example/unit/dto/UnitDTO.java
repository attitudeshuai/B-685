package com.example.unit.dto;

import jakarta.validation.constraints.*;

/**
 * 单位数据传输对象
 * 
 * @author System
 * @version 1.0.0
 */
public class UnitDTO {

    private Long id;

    @NotBlank(message = "单位名称不能为空")
    @Size(max = 100, message = "单位名称长度不能超过100个字符")
    private String name;

    @Size(max = 50, message = "单位编码长度不能超过50个字符")
    private String code;

    private Long parentId;

    @Size(max = 255, message = "单位地址长度不能超过255个字符")
    private String address;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    private String phone;

    @Email(message = "电子邮箱格式不正确")
    @Size(max = 100, message = "电子邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String leader;

    @Min(value = 0, message = "排序号不能小于0")
    private Integer sortOrder = 0;

    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    private Integer status = 1;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    public UnitDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
