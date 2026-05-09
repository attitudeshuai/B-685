package com.example.unit.service.impl;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.PositionDTO;
import com.example.unit.entity.Position;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.DepartmentRepository;
import com.example.unit.repository.PositionRepository;
import com.example.unit.service.PositionService;
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
public class PositionServiceImpl implements PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    public PositionServiceImpl(PositionRepository positionRepository, DepartmentRepository departmentRepository) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Position> findByPage(String name, Long deptId, Integer status, Integer page, Integer size) {
        String nameParam = StringUtils.hasText(name) ? name : null;
        Page<Position> result = positionRepository.findByConditions(nameParam, deptId, status, PageRequest.of(page - 1, size));
        return PageResult.of(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Position findById(Long id) {
        return positionRepository.findById(id).orElseThrow(() -> new BusinessException(404, "岗位不存在"));
    }

    @Override
    public Position create(PositionDTO dto) {
        logger.info("创建岗位: name={}", dto.getName());
        if (!departmentRepository.existsById(dto.getDeptId())) {
            throw new BusinessException(400, "所属部门不存在");
        }
        if (StringUtils.hasText(dto.getCode()) && positionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "岗位编码已存在");
        }

        Position pos = new Position();
        copyProperties(dto, pos);
        return positionRepository.save(pos);
    }

    @Override
    public Position update(Long id, PositionDTO dto) {
        logger.info("更新岗位: id={}", id);
        Position pos = positionRepository.findById(id).orElseThrow(() -> new BusinessException(404, "岗位不存在"));
        
        if (StringUtils.hasText(dto.getCode()) && positionRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new BusinessException(400, "岗位编码已存在");
        }

        copyProperties(dto, pos);
        return positionRepository.save(pos);
    }

    @Override
    public void delete(Long id) {
        logger.info("删除岗位: id={}", id);
        if (!positionRepository.existsById(id)) {
            throw new BusinessException(404, "岗位不存在");
        }
        positionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> findByDeptId(Long deptId) {
        return positionRepository.findByDeptIdOrderBySortOrderAsc(deptId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> findAll() {
        return positionRepository.findAllByOrderBySortOrderAsc();
    }

    private void copyProperties(PositionDTO dto, Position pos) {
        pos.setName(dto.getName());
        pos.setCode(dto.getCode());
        pos.setDeptId(dto.getDeptId());
        pos.setDescription(dto.getDescription());
        pos.setSortOrder(dto.getSortOrder());
        pos.setStatus(dto.getStatus());
    }
}
