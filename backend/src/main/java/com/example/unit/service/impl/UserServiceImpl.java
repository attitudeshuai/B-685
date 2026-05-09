package com.example.unit.service.impl;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.UserDTO;
import com.example.unit.entity.*;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.*;
import com.example.unit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UnitRepository unitRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository,
                           RoleRepository roleRepository, UnitRepository unitRepository,
                           DepartmentRepository departmentRepository, PositionRepository positionRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.unitRepository = unitRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserDTO> findByPage(String username, String nickname, Long unitId, Long deptId, Integer status, Integer page, Integer size) {
        String usernameParam = StringUtils.hasText(username) ? username : null;
        String nicknameParam = StringUtils.hasText(nickname) ? nickname : null;
        Page<SysUser> result = userRepository.findByConditions(usernameParam, nicknameParam, unitId, deptId, status, PageRequest.of(page - 1, size));
        
        List<UserDTO> dtos = result.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return PageResult.of(dtos, result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        SysUser user = userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return toDTO(user);
    }

    @Override
    public SysUser create(UserDTO dto) {
        logger.info("创建用户: username={}", dto.getUsername());
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        copyProperties(dto, user);
        // 简单密码处理（实际项目应使用BCrypt）
        user.setPassword(dto.getPassword() != null ? dto.getPassword() : "123456");
        
        SysUser saved = userRepository.save(user);
        
        // 保存用户角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                userRoleRepository.save(new UserRole(saved.getId(), roleId));
            }
        }
        
        return saved;
    }

    @Override
    public SysUser update(Long id, UserDTO dto) {
        logger.info("更新用户: id={}", id);
        SysUser user = userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        if (userRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
            throw new BusinessException(400, "用户名已存在");
        }

        copyProperties(dto, user);
        SysUser saved = userRepository.save(user);
        
        // 更新角色关联
        userRoleRepository.deleteByUserId(id);
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                userRoleRepository.save(new UserRole(id, roleId));
            }
        }
        
        return saved;
    }

    @Override
    public void delete(Long id) {
        logger.info("删除用户: id={}", id);
        if (!userRepository.existsById(id)) {
            throw new BusinessException(404, "用户不存在");
        }
        userRoleRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        logger.info("重置密码: id={}", id);
        SysUser user = userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysUser> findAll() {
        return userRepository.findAll();
    }

    private UserDTO toDTO(SysUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setUnitId(user.getUnitId());
        dto.setDeptId(user.getDeptId());
        dto.setPositionId(user.getPositionId());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setRemark(user.getRemark());

        // 填充关联名称
        if (user.getUnitId() != null) {
            unitRepository.findById(user.getUnitId()).ifPresent(u -> dto.setUnitName(u.getName()));
        }
        if (user.getDeptId() != null) {
            departmentRepository.findById(user.getDeptId()).ifPresent(d -> dto.setDeptName(d.getName()));
        }
        if (user.getPositionId() != null) {
            positionRepository.findById(user.getPositionId()).ifPresent(p -> dto.setPositionName(p.getName()));
        }

        // 填充角色
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        dto.setRoleIds(roleIds);
        if (!roleIds.isEmpty()) {
            List<String> roleNames = roleRepository.findAllById(roleIds).stream().map(Role::getName).collect(Collectors.toList());
            dto.setRoleNames(roleNames);
        }

        return dto;
    }

    private void copyProperties(UserDTO dto, SysUser user) {
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setUnitId(dto.getUnitId());
        user.setDeptId(dto.getDeptId());
        user.setPositionId(dto.getPositionId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setStatus(dto.getStatus());
        user.setRemark(dto.getRemark());
    }
}
