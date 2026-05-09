package com.example.unit.service;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.PositionDTO;
import com.example.unit.entity.Position;

import java.util.List;

public interface PositionService {

    PageResult<Position> findByPage(String name, Long deptId, Integer status, Integer page, Integer size);

    Position findById(Long id);

    Position create(PositionDTO dto);

    Position update(Long id, PositionDTO dto);

    void delete(Long id);

    List<Position> findByDeptId(Long deptId);

    List<Position> findAll();
}
