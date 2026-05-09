package com.example.unit.repository;

import com.example.unit.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByDeptIdOrderBySortOrderAsc(Long deptId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT p FROM Position p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:deptId IS NULL OR p.deptId = :deptId) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.sortOrder ASC")
    Page<Position> findByConditions(@Param("name") String name,
                                     @Param("deptId") Long deptId,
                                     @Param("status") Integer status,
                                     Pageable pageable);

    List<Position> findAllByOrderBySortOrderAsc();

    List<Position> findByStatusOrderBySortOrderAsc(Integer status);
}
