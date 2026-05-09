package com.example.unit.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门树形结构DTO
 */
public class DepartmentTreeDTO {

    private Long id;
    private String name;
    private String code;
    private Long unitId;
    private Long parentId;
    private String leader;
    private Integer sortOrder;
    private Integer status;
    private List<DepartmentTreeDTO> children = new ArrayList<>();

    public DepartmentTreeDTO() {}

    public DepartmentTreeDTO(Long id, String name, String code, Long unitId, Long parentId, String leader, Integer sortOrder, Integer status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.unitId = unitId;
        this.parentId = parentId;
        this.leader = leader;
        this.sortOrder = sortOrder;
        this.status = status;
    }

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
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<DepartmentTreeDTO> getChildren() { return children; }
    public void setChildren(List<DepartmentTreeDTO> children) { this.children = children; }
}
