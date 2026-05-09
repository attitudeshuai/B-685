package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.entity.Department;
import com.example.unit.service.DepartmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ApiResponse<PageResult<Department>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询部门列表: name={}, unitId={}", name, unitId);
        PageResult<Department> result = departmentService.findByPage(name, unitId, status, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Department> getById(@PathVariable Long id) {
        return ApiResponse.success(departmentService.findById(id));
    }

    @PostMapping
    public ApiResponse<Department> create(@Valid @RequestBody DepartmentDTO dto) {
        logger.info("创建部门: name={}", dto.getName());
        return ApiResponse.success("创建成功", departmentService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Department> update(@PathVariable Long id, @Valid @RequestBody DepartmentDTO dto) {
        logger.info("更新部门: id={}", id);
        return ApiResponse.success("更新成功", departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        logger.info("删除部门: id={}", id);
        departmentService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/tree")
    public ApiResponse<List<DepartmentTreeDTO>> tree(@RequestParam(required = false) Long unitId) {
        return ApiResponse.success(departmentService.getTree(unitId));
    }

    @GetMapping("/all")
    public ApiResponse<List<Department>> all() {
        return ApiResponse.success(departmentService.findAll());
    }
}
