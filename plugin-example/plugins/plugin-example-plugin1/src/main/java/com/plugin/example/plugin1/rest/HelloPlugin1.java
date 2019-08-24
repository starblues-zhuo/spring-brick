package com.plugin.example.plugin1.rest;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.plugin.example.main.InteractiveData;
import com.plugin.example.plugin1.config.PluginConfig1;
import com.plugin.example.plugin1.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping(path = "plugin1")
public class HelloPlugin1 {

    @Autowired
    private HelloService helloService;

    @Autowired
    private PluginConfig1 pluginConfig1;

    private EventBus eventBus;

    private Gson gson = new Gson();

    public HelloPlugin1(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @GetMapping("plugin1")
    public String sya(){
        return "hello plugin1 example";
    }

    @GetMapping("config")
    public String getConfig(){
        return pluginConfig1.toString();
    }


    @GetMapping("serviceConfig")
    public String getServiceConfig(){
        return helloService.getPluginConfig1().toString();
    }

    @GetMapping("service")
    public String getService(){
        return helloService.sayService2();
    }

    @GetMapping("service2")
    public String getPlugin2(){
        InteractiveData interactiveData = new InteractiveData();
        interactiveData.setName("my is plugin1");
        interactiveData.setAge(123);
        eventBus.post(interactiveData);
        return "ok";
    }

    /**
     * 依赖测试
     * @return
     */
    @GetMapping("dependency")
    public String gson(){
        return gson.toJson(pluginConfig1);
    }

}
