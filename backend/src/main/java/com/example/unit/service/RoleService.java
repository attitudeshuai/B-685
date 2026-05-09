package com.example.unit.service;

import com.example.unit.dto.PageResult;
import com.example.unit.dto.RoleDTO;
import com.example.unit.entity.Role;

import java.util.List;

public interface RoleService {

    PageResult<Role> findByPage(String name, Integer status, Integer page, Integer size);

    Role findById(Long id);

    Role create(RoleDTO dto);

    Role update(Long id, RoleDTO dto);

    void delete(Long id);

    List<Role> findAll();
}
