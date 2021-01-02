package com.mybatis.main.rest;

import com.mybatis.main.entity.User;
import com.mybatis.main.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/user")
public class UserResource {

    @Autowired
    private UserMapper userMapper;


    @GetMapping()
    public List<User> getUsers(){
        return userMapper.getList();
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") String id){
        return userMapper.getById(id);
    }

}
