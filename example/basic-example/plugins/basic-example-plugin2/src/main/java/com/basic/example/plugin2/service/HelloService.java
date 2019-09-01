package com.basic.example.plugin2.service;

import com.basic.example.plugin2.config.Plugin2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HelloService
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class HelloService {

    private final Plugin2Config plugin2Config;
    private final Service2 service2;

    @Autowired
    public HelloService(Plugin2Config plugin2Config, Service2 service2) {
        this.plugin2Config = plugin2Config;
        this.service2 = service2;
    }

    public Plugin2Config getPlugin2Config(){
        return plugin2Config;
    }


    public String sayService2(){
        return service2.getName();
    }

}
