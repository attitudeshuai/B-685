package com.example.unit.repository;

import com.example.unit.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 单位数据访问接口
 * 
 * @author System
 * @version 1.0.0
 */
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    /**
     * 根据单位编码查询
     */
    Optional<Unit> findByCode(String code);

    /**
     * 检查单位编码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 检查单位编码是否存在（排除指定ID）
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * 根据上级单位ID查询子单位列表
     */
    List<Unit> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 查询顶级单位列表
     */
    List<Unit> findByParentIdIsNullOrderBySortOrderAsc();

    /**
     * 根据状态查询单位列表
     */
    List<Unit> findByStatusOrderBySortOrderAsc(Integer status);

    /**
     * 分页查询单位列表
     */
    @Query("SELECT u FROM Unit u WHERE " +
           "(:name IS NULL OR u.name LIKE %:name%) AND " +
           "(:code IS NULL OR u.code LIKE %:code%) AND " +
           "(:status IS NULL OR u.status = :status) " +
           "ORDER BY u.sortOrder ASC, u.createTime DESC")
    Page<Unit> findByConditions(
            @Param("name") String name,
            @Param("code") String code,
            @Param("status") Integer status,
            Pageable pageable);

    /**
     * 查询所有单位（按排序号排序）
     */
    List<Unit> findAllByOrderBySortOrderAsc();

    /**
     * 检查是否有子单位
     */
    boolean existsByParentId(Long parentId);
}
