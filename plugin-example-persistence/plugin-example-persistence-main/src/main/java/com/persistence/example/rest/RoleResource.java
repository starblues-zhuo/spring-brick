package com.persistence.example.rest;

import com.persistence.example.entity.Role;
import com.persistence.example.mapper.RoleMapper;
import com.persistence.example.service.TestTestTransactional;
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
public class RoleResource {


    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private TestTestTransactional testTestTransactional;


    @GetMapping()
    public List<Role> getUsers(){
        return roleMapper.getList();
    }

    @GetMapping("/transactional")
    public void testTestTransactional(){
        testTestTransactional.transactional();
    }



}
