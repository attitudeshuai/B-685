package com.example.unit.service.impl;

import com.example.unit.dto.DepartmentDTO;
import com.example.unit.dto.DepartmentTreeDTO;
import com.example.unit.dto.PageResult;
import com.example.unit.entity.Department;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.DepartmentRepository;
import com.example.unit.repository.UnitRepository;
import com.example.unit.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;
    private final UnitRepository unitRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, UnitRepository unitRepository) {
        this.departmentRepository = departmentRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Department> findByPage(String name, Long unitId, Integer status, Integer page, Integer size) {
        String nameParam = StringUtils.hasText(name) ? name : null;
        Page<Department> result = departmentRepository.findByConditions(nameParam, unitId, status, PageRequest.of(page - 1, size));
        return PageResult.of(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new BusinessException(404, "部门不存在"));
    }

    @Override
    public Department create(DepartmentDTO dto) {
        logger.info("创建部门: name={}", dto.getName());
        if (!unitRepository.existsById(dto.getUnitId())) {
            throw new BusinessException(400, "所属单位不存在");
        }
        if (dto.getParentId() != null && !departmentRepository.existsById(dto.getParentId())) {
            throw new BusinessException(400, "上级部门不存在");
        }
        if (StringUtils.hasText(dto.getCode()) && departmentRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "部门编码已存在");
        }

        Department dept = new Department();
        copyProperties(dto, dept);
        return departmentRepository.save(dept);
    }

    @Override
    public Department update(Long id, DepartmentDTO dto) {
        logger.info("更新部门: id={}", id);
        Department dept = departmentRepository.findById(id).orElseThrow(() -> new BusinessException(404, "部门不存在"));
        
        if (StringUtils.hasText(dto.getCode()) && departmentRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new BusinessException(400, "部门编码已存在");
        }
        if (dto.getParentId() != null && dto.getParentId().equals(id)) {
            throw new BusinessException(400, "上级部门不能是自己");
        }

        copyProperties(dto, dept);
        return departmentRepository.save(dept);
    }

    @Override
    public void delete(Long id) {
        logger.info("删除部门: id={}", id);
        if (!departmentRepository.existsById(id)) {
            throw new BusinessException(404, "部门不存在");
        }
        if (departmentRepository.existsByParentId(id)) {
            throw new BusinessException(400, "存在下级部门，无法删除");
        }
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentTreeDTO> getTree(Long unitId) {
        List<Department> all = unitId != null ? 
            departmentRepository.findByUnitIdOrderBySortOrderAsc(unitId) : 
            departmentRepository.findAllByOrderBySortOrderAsc();

        List<DepartmentTreeDTO> dtos = all.stream()
            .map(d -> new DepartmentTreeDTO(d.getId(), d.getName(), d.getCode(), d.getUnitId(), d.getParentId(), d.getLeader(), d.getSortOrder(), d.getStatus()))
            .collect(Collectors.toList());

        Map<Long, DepartmentTreeDTO> map = dtos.stream().collect(Collectors.toMap(DepartmentTreeDTO::getId, d -> d));
        List<DepartmentTreeDTO> roots = new ArrayList<>();

        for (DepartmentTreeDTO dto : dtos) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                DepartmentTreeDTO parent = map.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                } else {
                    roots.add(dto);
                }
            }
        }
        return roots;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> findByUnitId(Long unitId) {
        return departmentRepository.findByUnitIdOrderBySortOrderAsc(unitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAllByOrderBySortOrderAsc();
    }

    private void copyProperties(DepartmentDTO dto, Department dept) {
        dept.setName(dto.getName());
        dept.setCode(dto.getCode());
        dept.setUnitId(dto.getUnitId());
        dept.setParentId(dto.getParentId());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setEmail(dto.getEmail());
        dept.setSortOrder(dto.getSortOrder());
        dept.setStatus(dto.getStatus());
        dept.setRemark(dto.getRemark());
    }
}
