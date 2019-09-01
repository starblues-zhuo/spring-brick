package com.basic.example.plugin1.service;

import org.springframework.stereotype.Component;


/**
 * Service2
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component("plugin2Service2")
public class Service2 {

    public String getName(){
        return Service2.class.getName();
    }

}
