package com.plugin.example.plugin2.service;

import com.plugin.example.plugin2.config.Plugin2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:05
 * @Update Date Time:
 * @see
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
