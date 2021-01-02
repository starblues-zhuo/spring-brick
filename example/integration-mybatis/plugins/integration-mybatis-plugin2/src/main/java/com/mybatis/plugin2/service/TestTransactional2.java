package com.mybatis.plugin2.service;

import com.mybatis.plugin2.mapper.Plugin2Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Component
public class TestTransactional2 {



    @Autowired
    private Plugin2Mapper plugin2Mapper;


    @Transactional(rollbackFor = Exception.class)
    public void transactional(){
        plugin2Mapper.save(String.valueOf(System.currentTimeMillis()), "123");
        plugin2Mapper.save(String.valueOf(System.currentTimeMillis()), "1234");
        int a = 1/0;
        plugin2Mapper.save(String.valueOf(System.currentTimeMillis()), "13");
    }


}
