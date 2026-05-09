package com.example.unit.repository;

import com.example.unit.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
}
