package com.plugin.example.plugin1.rest;

import com.google.gson.Gson;
import com.plugin.example.plugin1.config.PluginConfig;
import com.plugin.example.plugin1.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:04
 * @Update Date Time:
 * @see
 */
@RestController
@RequestMapping("/plugin1")
public class HelloPlugin1 {

    @Autowired
    private HelloService helloService;

    @Autowired
    private PluginConfig pluginConfig;

    private Gson gson = new Gson();

    @GetMapping
    public String sya(){
        return "hello plugin1 example";
    }

    @GetMapping("config")
    public String getConfig(){
        return pluginConfig.toString();
    }


    @GetMapping("serviceConfig")
    public String getServiceConfig(){
        return helloService.getPluginConfig().toString();
    }

    @GetMapping("service")
    public String getService(){
        return helloService.sayService2();
    }

    /**
     * 依赖测试
     * @return
     */
    @GetMapping("dependency")
    public String gson(){
        return gson.toJson(pluginConfig);
    }

}
