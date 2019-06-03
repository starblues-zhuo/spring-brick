package com.plugin.example.plugin1.service;

import com.plugin.example.plugin1.config.PluginConfig;
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

    private final PluginConfig pluginConfig;
    private final Service2 service2;

    @Autowired
    public HelloService(PluginConfig pluginConfig, Service2 service2) {
        this.pluginConfig = pluginConfig;
        this.service2 = service2;
    }

    public PluginConfig getPluginConfig(){
        return pluginConfig;
    }


    public String sayService2(){
        return service2.getName();
    }

}
