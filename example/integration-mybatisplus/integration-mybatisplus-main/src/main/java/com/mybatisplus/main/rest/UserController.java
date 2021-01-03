package com.mybatisplus.main.rest;

import com.mybatisplus.main.entity.User;
import com.mybatisplus.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public User save(){
        User user = new User();
        user.setName("test");
        user.setPassword("123");
        user.setUsername("user");
        userService.save(user);
        return user;
    }

    @GetMapping
    public List<User> getAll(){
        return userService.list();
    }

}
