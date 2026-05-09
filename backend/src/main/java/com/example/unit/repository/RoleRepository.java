package com.example.unit.repository;

import com.example.unit.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT r FROM Role r WHERE " +
           "(:name IS NULL OR r.name LIKE %:name%) AND " +
           "(:status IS NULL OR r.status = :status) " +
           "ORDER BY r.sortOrder ASC")
    Page<Role> findByConditions(@Param("name") String name,
                                 @Param("status") Integer status,
                                 Pageable pageable);

    List<Role> findAllByOrderBySortOrderAsc();

    List<Role> findByStatusOrderBySortOrderAsc(Integer status);
}
