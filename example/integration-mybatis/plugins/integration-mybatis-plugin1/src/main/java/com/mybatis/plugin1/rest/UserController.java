package com.mybatis.plugin1.rest;

import com.mybatis.main.entity.User;
import com.mybatis.main.mapper.UserMapper;
import com.mybatis.main.service.TestTestTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
