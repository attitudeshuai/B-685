package com.example.unit.dto;

import jakarta.validation.constraints.*;

/**
 * 角色数据传输对象
 */
public class RoleDTO {

    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String name;

    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String code;

    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    @Min(value = 0, message = "排序号不能小于0")
    private Integer sortOrder = 0;

    private Integer status = 1;

    public RoleDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
