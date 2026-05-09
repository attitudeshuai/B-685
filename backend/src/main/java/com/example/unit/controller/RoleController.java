package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.entity.Role;
import com.example.unit.service.RoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<PageResult<Role>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询角色列表: name={}", name);
        PageResult<Role> result = roleService.findByPage(name, status, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Role> getById(@PathVariable Long id) {
        return ApiResponse.success(roleService.findById(id));
    }

    @PostMapping
    public ApiResponse<Role> create(@Valid @RequestBody RoleDTO dto) {
        logger.info("创建角色: name={}", dto.getName());
        return ApiResponse.success("创建成功", roleService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Role> update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        logger.info("更新角色: id={}", id);
        return ApiResponse.success("更新成功", roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        logger.info("删除角色: id={}", id);
        roleService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/all")
    public ApiResponse<List<Role>> all() {
        return ApiResponse.success(roleService.findAll());
    }
}
