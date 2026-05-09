package com.example.unit.repository;

import com.example.unit.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByUnitIdOrderBySortOrderAsc(Long unitId);

    List<Department> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Department> findByUnitIdAndParentIdIsNullOrderBySortOrderAsc(Long unitId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByParentId(Long parentId);

    @Query("SELECT d FROM Department d WHERE " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:unitId IS NULL OR d.unitId = :unitId) AND " +
           "(:status IS NULL OR d.status = :status) " +
           "ORDER BY d.sortOrder ASC")
    Page<Department> findByConditions(@Param("name") String name,
                                       @Param("unitId") Long unitId,
                                       @Param("status") Integer status,
                                       Pageable pageable);

    List<Department> findAllByOrderBySortOrderAsc();
}
