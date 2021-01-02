package com.mybatisplus.main.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatisplus.main.entity.User;
import com.mybatisplus.main.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
