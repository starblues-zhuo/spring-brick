package com.plugin.example.plugin1.service;

import com.plugin.example.plugin1.config.PluginConfig1;
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
@Component("plugin2HelloService")
public class HelloService {

    private final PluginConfig1 pluginConfig1;
    private final Service2 service2;

    @Autowired
    public HelloService(PluginConfig1 pluginConfig1, Service2 service2) {
        this.pluginConfig1 = pluginConfig1;
        this.service2 = service2;
    }

    public PluginConfig1 getPluginConfig1(){
        return pluginConfig1;
    }


    public String sayService2(){
        return service2.getName();
    }

}
