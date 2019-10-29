package com.mybatis.plugin1.service;

import com.mybatis.plugin1.mapper.Plugin1Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class TestTransactional1 implements TranServiec{


    @Autowired
    private Plugin1Mapper pluginMapperl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactional(){
        pluginMapperl.insert(String.valueOf(System.currentTimeMillis()), "123");
        pluginMapperl.insert(String.valueOf(System.currentTimeMillis()), "1234");
        int a = 1/0;
        pluginMapperl.insert(String.valueOf(System.currentTimeMillis()), "13");
    }

}
