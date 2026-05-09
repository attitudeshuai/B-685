package com.example.unit.controller;

import com.example.unit.dto.ApiResponse;
import com.example.unit.entity.SysUser;
import com.example.unit.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        logger.info("User login attempt: {}", username);

        Optional<SysUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        SysUser user = userOpt.get();
        if (!password.equals(user.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return ApiResponse.error(403, "用户已被禁用");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("phone", user.getPhone());
        result.put("email", user.getEmail());
        result.put("unitId", user.getUnitId());
        result.put("deptId", user.getDeptId());

        logger.info("User {} logged in successfully", username);
        return ApiResponse.success(result);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        logger.info("User logout");
        return ApiResponse.success(null);
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> registerForm) {
        String username = registerForm.get("username");
        String password = registerForm.get("password");
        String nickname = registerForm.get("nickname");

        logger.info("User registration attempt: {}", username);

        // Check if username exists
        Optional<SysUser> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return ApiResponse.error(400, "用户名已存在");
        }

        // Create new user
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setStatus(1);
        user = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());

        logger.info("User {} registered successfully", username);
        return ApiResponse.success(result);
    }

    @GetMapping("/profile")
    public ApiResponse<SysUser> getProfile(@RequestParam Long userId) {
        Optional<SysUser> userOpt = userRepository.findById(userId);
        return userOpt.map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.error(404, "用户不存在"));
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(@RequestBody Map<String, Object> profileData) {
        Long userId = Long.valueOf(profileData.get("id").toString());
        Optional<SysUser> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ApiResponse.error(404, "用户不存在");
        }

        SysUser user = userOpt.get();
        if (profileData.containsKey("nickname")) {
            user.setNickname((String) profileData.get("nickname"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        userRepository.save(user);

        logger.info("User {} updated profile", user.getUsername());
        return ApiResponse.success(null);
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@RequestBody Map<String, String> passwordData) {
        String userId = passwordData.get("userId");
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        Optional<SysUser> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return ApiResponse.error(404, "用户不存在");
        }

        SysUser user = userOpt.get();
        if (!oldPassword.equals(user.getPassword())) {
            return ApiResponse.error(400, "原密码错误");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        logger.info("User {} changed password", user.getUsername());
        return ApiResponse.success(null);
    }
}
