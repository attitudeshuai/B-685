package com.example.unit.repository;

import com.example.unit.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    @Query("SELECT u FROM SysUser u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:nickname IS NULL OR u.nickname LIKE %:nickname%) AND " +
           "(:unitId IS NULL OR u.unitId = :unitId) AND " +
           "(:deptId IS NULL OR u.deptId = :deptId) AND " +
           "(:status IS NULL OR u.status = :status) " +
           "ORDER BY u.createTime DESC")
    Page<SysUser> findByConditions(@Param("username") String username,
                                    @Param("nickname") String nickname,
                                    @Param("unitId") Long unitId,
                                    @Param("deptId") Long deptId,
                                    @Param("status") Integer status,
                                    Pageable pageable);
}
