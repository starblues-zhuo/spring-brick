package com.plugin.example.start.rest;

import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.plugin.example.start.plugin.ConsoleName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 15:54
 * @Update Date Time:
 * @see
 */
@RestController
@RequestMapping(path = "/hello")
public class HelloResource {


    private PluginUser pluginUser;

    public HelloResource(PluginApplication pluginApplication) {
        //this.pluginUser = pluginApplication.getPluginUser();
    }

    @GetMapping
    public String sya(){
        return "hello spring boot plugin example";
    }

    @GetMapping("/consoleName")
    public String consoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = pluginUser
                .getSpringDefineBeansOfType(ConsoleName.class);
        for (ConsoleName consoleName : consoleNames) {
            stringBuffer.append(consoleName.name())
                    .append("<br/>");
        }
        return stringBuffer.toString();
    }


    @GetMapping("/pluginName")
    public String pluginName(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = pluginUser
                .getPluginSpringDefineBeansOfType(ConsoleName.class);
        for (ConsoleName consoleName : consoleNames) {
            stringBuffer.append(consoleName.name())
                    .append("<br/>");
        }
        System.out.println(stringBuffer);
        return stringBuffer.toString();
    }

    @GetMapping("/test")
    public void test(){
        try {
            Class<?> aClass = Class.forName("com.plugin.example.plugin1.Test");
            Object o = aClass.newInstance();
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
