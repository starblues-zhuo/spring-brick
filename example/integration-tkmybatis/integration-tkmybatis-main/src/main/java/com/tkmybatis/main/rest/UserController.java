package com.tkmybatis.main.rest;

import com.google.common.collect.Lists;
import com.tkmybatis.main.entity.User;
import com.tkmybatis.main.mapper.UserMapper;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;


    @GetMapping
    public List<User> getUser(){
        return userMapper.selectAll();
    }



}
