package com.example.unit.service;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.UserDTO;
import com.example.unit.entity.SysUser;

import java.util.List;

public interface UserService {

    PageResult<UserDTO> findByPage(String username, String nickname, Long unitId, Long deptId, Integer status, Integer page, Integer size);

    UserDTO findById(Long id);

    SysUser create(UserDTO dto);

    SysUser update(Long id, UserDTO dto);

    void delete(Long id);

    void resetPassword(Long id, String newPassword);

    List<SysUser> findAll();
}
