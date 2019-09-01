package com.basic.example.plugin2.service;

import org.springframework.stereotype.Component;

/**
 * Service2
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class Service2 {

    public String getName(){
        return Service2.class.getName();
    }

}
