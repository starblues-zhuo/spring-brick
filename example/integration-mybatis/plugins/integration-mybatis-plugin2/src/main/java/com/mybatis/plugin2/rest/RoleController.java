package com.mybatis.plugin2.rest;

import com.gitee.starblues.realize.PluginUtils;
import com.mybatis.main.entity.Role;
import com.mybatis.main.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(PluginUtils pluginUtils){
        roleMapper = pluginUtils.getMainBean(RoleMapper.class);
    }

    @GetMapping("/list")
    public List<Role> getList(){
        return roleMapper.getList();
    }



}
