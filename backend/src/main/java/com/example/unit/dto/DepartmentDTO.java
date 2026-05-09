package com.example.unit.dto;

import jakarta.validation.constraints.*;

/**
 * 部门数据传输对象
 */
public class DepartmentDTO {

    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String name;

    @Size(max = 50, message = "部门编码长度不能超过50个字符")
    private String code;

    @NotNull(message = "所属单位不能为空")
    private Long unitId;

    private Long parentId;

    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String leader;

    private String phone;
    private String email;

    @Min(value = 0, message = "排序号不能小于0")
    private Integer sortOrder = 0;

    private Integer status = 1;
    private String remark;

    public DepartmentDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
