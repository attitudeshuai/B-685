package com.example.unit.service;

import com.example.unit.dto.DepartmentDTO;
import com.example.unit.dto.DepartmentTreeDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.Department;

import java.util.List;

public interface DepartmentService {

    PageResult<Department> findByPage(String name, Long unitId, Integer status, Integer page, Integer size);

    Department findById(Long id);

    Department create(DepartmentDTO dto);

    Department update(Long id, DepartmentDTO dto);

    void delete(Long id);

    List<DepartmentTreeDTO> getTree(Long unitId);

    List<Department> findByUnitId(Long unitId);

    List<Department> findAll();
}
