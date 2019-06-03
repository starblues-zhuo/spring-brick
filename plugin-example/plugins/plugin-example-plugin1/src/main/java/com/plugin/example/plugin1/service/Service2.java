package com.plugin.example.plugin1.service;

import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:08
 * @Update Date Time:
 * @see
 */
@Component
public class Service2 {

    public String getName(){
        return Service2.class.getName();
    }

}
