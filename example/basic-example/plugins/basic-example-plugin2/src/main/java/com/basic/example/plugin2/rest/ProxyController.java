package com.basic.example.plugin2.rest;

import com.basic.example.main.invokeapi.CommonParam;
import com.basic.example.main.invokeapi.CommonReturn;
import com.basic.example.plugin2.service.CallerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("/proxy")
public class ProxyController {


    @Autowired
    private CallerService callerService;

    @GetMapping("getConfig")
    public CallerService.PluginInfo proxy(){
        return callerService.getConfig("plugin2");
    }


    @GetMapping("add")
    public Integer add(){
        return callerService.add(1, 2);
    }

    @GetMapping("call")
    public String call(){
        CallerService.CallerInfo callerInfo = new CallerService.CallerInfo();
        callerInfo.setName("test call");
        callerInfo.setPluginInfo(callerService.getConfig("plugin2"));
        return callerService.test(callerInfo, "hello, my name is plugin1");
    }


    @GetMapping("common")
    public CommonReturn common(){
        CommonParam commonParam = new CommonParam();
        commonParam.setName("my name is plugin1");
        commonParam.setAge(24);
        return callerService.commonTest("commonTest", commonParam);
    }
}
