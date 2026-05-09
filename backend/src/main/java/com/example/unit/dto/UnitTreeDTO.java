package com.example.unit.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 单位树形结构DTO
 * 
 * @author System
 * @version 1.0.0
 */
public class UnitTreeDTO {

    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
    private List<UnitTreeDTO> children = new ArrayList<>();

    public UnitTreeDTO() {}

    public UnitTreeDTO(Long id, String name, String code, Long parentId, Integer sortOrder, Integer status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
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
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<UnitTreeDTO> getChildren() { return children; }
    public void setChildren(List<UnitTreeDTO> children) { this.children = children; }
}
