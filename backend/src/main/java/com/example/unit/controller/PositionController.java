package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.entity.Position;
import com.example.unit.service.PositionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public ApiResponse<PageResult<Position>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询岗位列表: name={}, deptId={}", name, deptId);
        PageResult<Position> result = positionService.findByPage(name, deptId, status, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Position> getById(@PathVariable Long id) {
        return ApiResponse.success(positionService.findById(id));
    }

    @PostMapping
    public ApiResponse<Position> create(@Valid @RequestBody PositionDTO dto) {
        logger.info("创建岗位: name={}", dto.getName());
        return ApiResponse.success("创建成功", positionService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Position> update(@PathVariable Long id, @Valid @RequestBody PositionDTO dto) {
        logger.info("更新岗位: id={}", id);
        return ApiResponse.success("更新成功", positionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        logger.info("删除岗位: id={}", id);
        positionService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/all")
    public ApiResponse<List<Position>> all() {
        return ApiResponse.success(positionService.findAll());
    }

    @GetMapping("/by-dept/{deptId}")
    public ApiResponse<List<Position>> byDept(@PathVariable Long deptId) {
        return ApiResponse.success(positionService.findByDeptId(deptId));
    }
}
