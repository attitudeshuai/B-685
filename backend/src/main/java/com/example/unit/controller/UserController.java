package com.example.unit.controller;

import com.example.unit.dto.*;
import com.example.unit.entity.SysUser;
import com.example.unit.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResult<UserDTO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("查询用户列表: username={}, nickname={}", username, nickname);
        PageResult<UserDTO> result = userService.findByPage(username, nickname, unitId, deptId, status, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(userService.findById(id));
    }

    @PostMapping
    public ApiResponse<SysUser> create(@Valid @RequestBody UserDTO dto) {
        logger.info("创建用户: username={}", dto.getUsername());
        return ApiResponse.success("创建成功", userService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<SysUser> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        logger.info("更新用户: id={}", id);
        return ApiResponse.success("更新成功", userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        logger.info("删除用户: id={}", id);
        userService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        logger.info("重置密码: id={}", id);
        String newPassword = body.getOrDefault("password", "123456");
        userService.resetPassword(id, newPassword);
        return ApiResponse.success("密码重置成功", null);
    }
}
