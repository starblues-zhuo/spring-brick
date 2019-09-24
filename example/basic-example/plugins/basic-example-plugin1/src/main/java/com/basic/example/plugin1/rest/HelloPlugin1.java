package com.basic.example.plugin1.rest;

import com.basic.example.plugin1.config.PluginConfig1;
import com.basic.example.plugin1.service.HelloService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "插件接口", description = "插件hello案例")
public class HelloPlugin1 {

    @Autowired
    private HelloService helloService;

    @Autowired
    private PluginConfig1 pluginConfig1;


    @GetMapping("plugin1")
    @ApiOperation(value = "hello", notes = "hello")
    public String sya(){
        return "hello plugin1 example";
    }

    @GetMapping("config")
    @ApiOperation(value = "getConfig", notes = "得到配置文件")
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



}
