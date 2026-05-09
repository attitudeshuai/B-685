package com.example.unit.service.impl;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.RoleDTO;
import com.example.unit.entity.Role;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.RoleRepository;
import com.example.unit.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Role> findByPage(String name, Integer status, Integer page, Integer size) {
        String nameParam = StringUtils.hasText(name) ? name : null;
        Page<Role> result = roleRepository.findByConditions(nameParam, status, PageRequest.of(page - 1, size));
        return PageResult.of(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new BusinessException(404, "角色不存在"));
    }

    @Override
    public Role create(RoleDTO dto) {
        logger.info("创建角色: name={}", dto.getName());
        if (StringUtils.hasText(dto.getCode()) && roleRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "角色编码已存在");
        }

        Role role = new Role();
        copyProperties(dto, role);
        return roleRepository.save(role);
    }

    @Override
    public Role update(Long id, RoleDTO dto) {
        logger.info("更新角色: id={}", id);
        Role role = roleRepository.findById(id).orElseThrow(() -> new BusinessException(404, "角色不存在"));
        
        if (StringUtils.hasText(dto.getCode()) && roleRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new BusinessException(400, "角色编码已存在");
        }

        copyProperties(dto, role);
        return roleRepository.save(role);
    }

    @Override
    public void delete(Long id) {
        logger.info("删除角色: id={}", id);
        if (!roleRepository.existsById(id)) {
            throw new BusinessException(404, "角色不存在");
        }
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAllByOrderBySortOrderAsc();
    }

    private void copyProperties(RoleDTO dto, Role role) {
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setSortOrder(dto.getSortOrder());
        role.setStatus(dto.getStatus());
    }
}
