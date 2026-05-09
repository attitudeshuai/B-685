package com.example.unit.service;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.UnitDTO;
import com.example.unit.dto.UnitTreeDTO;
import com.example.unit.entity.Unit;

import java.util.List;

/**
 * 单位服务接口
 * 
 * @author System
 * @version 1.0.0
 */
public interface UnitService {

    /**
     * 分页查询单位列表
     */
    PageResult<Unit> findByPage(String name, String code, Integer status, Integer page, Integer size);

    /**
     * 根据ID查询单位详情
     */
    Unit findById(Long id);

    /**
     * 创建单位
     */
    Unit create(UnitDTO unitDTO);

    /**
     * 更新单位
     */
    Unit update(Long id, UnitDTO unitDTO);

    /**
     * 删除单位
     */
    void delete(Long id);

    /**
     * 获取单位树形结构
     */
    List<UnitTreeDTO> getTree();

    /**
     * 获取所有单位列表
     */
    List<Unit> findAll();

    /**
     * 检查单位编码是否存在
     */
    boolean existsByCode(String code, Long excludeId);
}
