package com.example.unit.service.impl;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.UnitDTO;
import com.example.unit.dto.UnitTreeDTO;
import com.example.unit.entity.Unit;
import com.example.unit.exception.BusinessException;
import com.example.unit.repository.UnitRepository;
import com.example.unit.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 单位服务实现类
 * 
 * @author System
 * @version 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UnitServiceImpl implements UnitService {

    private static final Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Unit> findByPage(String name, String code, Integer status, Integer page, Integer size) {
        logger.debug("分页查询单位列表: name={}, code={}, status={}, page={}, size={}", name, code, status, page, size);
        
        // 处理空字符串
        String nameParam = StringUtils.hasText(name) ? name : null;
        String codeParam = StringUtils.hasText(code) ? code : null;
        
        Page<Unit> pageResult = unitRepository.findByConditions(
                nameParam, codeParam, status,
                PageRequest.of(page - 1, size));
        
        return PageResult.of(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                size);
    }

    @Override
    @Transactional(readOnly = true)
    public Unit findById(Long id) {
        logger.debug("根据ID查询单位: id={}", id);
        return unitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "单位不存在"));
    }

    @Override
    public Unit create(UnitDTO unitDTO) {
        logger.info("创建单位: name={}, code={}", unitDTO.getName(), unitDTO.getCode());
        
        // 校验单位编码唯一性
        if (StringUtils.hasText(unitDTO.getCode()) && unitRepository.existsByCode(unitDTO.getCode())) {
            throw new BusinessException(400, "单位编码已存在");
        }
        
        // 校验上级单位是否存在
        if (unitDTO.getParentId() != null && !unitRepository.existsById(unitDTO.getParentId())) {
            throw new BusinessException(400, "上级单位不存在");
        }
        
        Unit unit = new Unit();
        BeanUtils.copyProperties(unitDTO, unit);
        
        Unit savedUnit = unitRepository.save(unit);
        logger.info("创建单位成功: id={}, name={}", savedUnit.getId(), savedUnit.getName());
        
        return savedUnit;
    }

    @Override
    public Unit update(Long id, UnitDTO unitDTO) {
        logger.info("更新单位: id={}, name={}", id, unitDTO.getName());
        
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "单位不存在"));
        
        // 校验单位编码唯一性
        if (StringUtils.hasText(unitDTO.getCode()) && 
            unitRepository.existsByCodeAndIdNot(unitDTO.getCode(), id)) {
            throw new BusinessException(400, "单位编码已存在");
        }
        
        // 校验上级单位是否存在
        if (unitDTO.getParentId() != null) {
            if (unitDTO.getParentId().equals(id)) {
                throw new BusinessException(400, "上级单位不能是自己");
            }
            if (!unitRepository.existsById(unitDTO.getParentId())) {
                throw new BusinessException(400, "上级单位不存在");
            }
            // 检查是否形成循环引用
            if (isCircularReference(id, unitDTO.getParentId())) {
                throw new BusinessException(400, "不能将下级单位设为上级单位");
            }
        }
        
        // 更新属性
        unit.setName(unitDTO.getName());
        unit.setCode(unitDTO.getCode());
        unit.setParentId(unitDTO.getParentId());
        unit.setAddress(unitDTO.getAddress());
        unit.setPhone(unitDTO.getPhone());
        unit.setEmail(unitDTO.getEmail());
        unit.setLeader(unitDTO.getLeader());
        unit.setSortOrder(unitDTO.getSortOrder());
        unit.setStatus(unitDTO.getStatus());
        unit.setRemark(unitDTO.getRemark());
        
        Unit savedUnit = unitRepository.save(unit);
        logger.info("更新单位成功: id={}, name={}", savedUnit.getId(), savedUnit.getName());
        
        return savedUnit;
    }

    @Override
    public void delete(Long id) {
        logger.info("删除单位: id={}", id);
        
        if (!unitRepository.existsById(id)) {
            throw new BusinessException(404, "单位不存在");
        }
        
        // 检查是否有子单位
        if (unitRepository.existsByParentId(id)) {
            throw new BusinessException(400, "存在下级单位，无法删除");
        }
        
        unitRepository.deleteById(id);
        logger.info("删除单位成功: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitTreeDTO> getTree() {
        logger.debug("获取单位树形结构");
        
        List<Unit> allUnits = unitRepository.findAllByOrderBySortOrderAsc();
        
        // 转换为TreeDTO
        List<UnitTreeDTO> allDTOs = allUnits.stream()
                .map(u -> new UnitTreeDTO(u.getId(), u.getName(), u.getCode(), 
                        u.getParentId(), u.getSortOrder(), u.getStatus()))
                .collect(Collectors.toList());
        
        // 构建树形结构
        Map<Long, UnitTreeDTO> dtoMap = allDTOs.stream()
                .collect(Collectors.toMap(UnitTreeDTO::getId, dto -> dto));
        
        List<UnitTreeDTO> rootList = new ArrayList<>();
        
        for (UnitTreeDTO dto : allDTOs) {
            if (dto.getParentId() == null) {
                rootList.add(dto);
            } else {
                UnitTreeDTO parent = dtoMap.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                } else {
                    // 如果找不到父节点，作为根节点处理
                    rootList.add(dto);
                }
            }
        }
        
        return rootList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> findAll() {
        logger.debug("获取所有单位列表");
        return unitRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        if (excludeId != null) {
            return unitRepository.existsByCodeAndIdNot(code, excludeId);
        }
        return unitRepository.existsByCode(code);
    }

    /**
     * 检查是否形成循环引用
     */
    private boolean isCircularReference(Long unitId, Long newParentId) {
        Set<Long> visited = new HashSet<>();
        Long currentId = newParentId;
        
        while (currentId != null) {
            if (visited.contains(currentId)) {
                return true; // 已经访问过，存在循环
            }
            if (currentId.equals(unitId)) {
                return true; // 新的父节点链中包含当前节点
            }
            visited.add(currentId);
            
            Optional<Unit> parent = unitRepository.findById(currentId);
            currentId = parent.map(Unit::getParentId).orElse(null);
        }
        
        return false;
    }
}
