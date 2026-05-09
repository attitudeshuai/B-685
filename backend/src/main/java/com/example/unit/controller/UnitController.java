package com.example.unit.controller;

import com.example.unit.dto.ApiResponse;
import com.example.unit.dto.PageResult;
import com.example.unit.dto.UnitDTO;
import com.example.unit.dto.UnitTreeDTO;
import com.example.unit.entity.Unit;
import com.example.unit.service.UnitService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单位管理控制器
 * 
 * @author System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/units")
public class UnitController {

    private static final Logger logger = LoggerFactory.getLogger(UnitController.class);

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * 分页查询单位列表
     */
    @GetMapping
    public ApiResponse<PageResult<Unit>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.debug("分页查询单位列表: name={}, code={}, status={}, page={}, size={}", 
                name, code, status, page, size);
        PageResult<Unit> result = unitService.findByPage(name, code, status, page, size);
        return ApiResponse.success(result);
    }

    /**
     * 获取所有单位列表
     */
    @GetMapping("/all")
    public ApiResponse<List<Unit>> listAll() {
        logger.debug("获取所有单位列表");
        List<Unit> units = unitService.findAll();
        return ApiResponse.success(units);
    }

    /**
     * 获取单位树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<UnitTreeDTO>> tree() {
        logger.debug("获取单位树形结构");
        List<UnitTreeDTO> tree = unitService.getTree();
        return ApiResponse.success(tree);
    }

    /**
     * 根据ID查询单位详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Unit> getById(@PathVariable Long id) {
        logger.debug("查询单位详情: id={}", id);
        Unit unit = unitService.findById(id);
        return ApiResponse.success(unit);
    }

    /**
     * 创建单位
     */
    @PostMapping
    public ApiResponse<Unit> create(@Valid @RequestBody UnitDTO unitDTO) {
        logger.info("创建单位: name={}", unitDTO.getName());
        Unit unit = unitService.create(unitDTO);
        return ApiResponse.success("创建成功", unit);
    }

    /**
     * 更新单位
     */
    @PutMapping("/{id}")
    public ApiResponse<Unit> update(@PathVariable Long id, @Valid @RequestBody UnitDTO unitDTO) {
        logger.info("更新单位: id={}, name={}", id, unitDTO.getName());
        Unit unit = unitService.update(id, unitDTO);
        return ApiResponse.success("更新成功", unit);
    }

    /**
     * 删除单位
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        logger.info("删除单位: id={}", id);
        unitService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 检查单位编码是否存在
     */
    @GetMapping("/check-code")
    public ApiResponse<Boolean> checkCode(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        logger.debug("检查单位编码: code={}, excludeId={}", code, excludeId);
        boolean exists = unitService.existsByCode(code, excludeId);
        return ApiResponse.success(exists);
    }
}
