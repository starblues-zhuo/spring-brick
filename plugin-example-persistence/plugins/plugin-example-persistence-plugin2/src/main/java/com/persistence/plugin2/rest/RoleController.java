package com.persistence.plugin2.rest;

import com.persistence.example.entity.Role;
import com.persistence.example.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @GetMapping("/list")
    public List<Role> getList(){
        return roleMapper.getList();
    }



}
