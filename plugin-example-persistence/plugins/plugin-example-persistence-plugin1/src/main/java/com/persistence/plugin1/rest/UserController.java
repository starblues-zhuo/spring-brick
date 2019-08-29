package com.persistence.plugin1.rest;

import com.persistence.example.entity.User;
import com.persistence.example.mapper.UserMapper;
import com.persistence.example.service.TestTestTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final UserMapper userMapper;
    private final TestTestTransactional testTestTransactional;

    @Autowired
    public UserController(UserMapper userMapper, TestTestTransactional testTestTransactional) {
        this.userMapper = userMapper;
        this.testTestTransactional = testTestTransactional;
    }

    @GetMapping("/list")
    public List<User> getList(){
        return userMapper.getList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") String id){
        return userMapper.getById(id);
    }


    @GetMapping("/transactional")
    public void testTestTransactional(){
        testTestTransactional.transactional();
    }
}
