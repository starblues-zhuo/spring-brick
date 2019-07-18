package com.plugin.example.main.rest;

import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.plugin.example.main.plugin.ConsoleName;
import com.plugin.example.main.plugin.ConsoleNameFactory;
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


    private final PluginUser pluginUser;
    private final ConsoleNameFactory consoleNameFactory;

    public HelloResource(PluginApplication pluginApplication,
                         ConsoleNameFactory consoleNameFactory) {
        this.pluginUser = pluginApplication.getPluginUser();
        this.consoleNameFactory = consoleNameFactory;
    }

    @GetMapping
    public String sya(){
        return "hello spring boot plugin example";
    }

    @GetMapping("/consoleName")
    public String consoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = pluginUser.getBeans(ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }


    @GetMapping("/pluginConsoleName")
    public String pluginConsoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = pluginUser.getPluginBeans(ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }

    @GetMapping("/pluginConsoleName2")
    public String pluginConsoleName2(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = consoleNameFactory.getBeans();
        return getConsoleNames(stringBuffer, consoleNames);
    }

    private String getConsoleNames(StringBuffer stringBuffer, List<ConsoleName> consoleNames) {
        for (ConsoleName consoleName : consoleNames) {
            stringBuffer.append(consoleName.name())
                    .append("<br/>");
        }
        return stringBuffer.toString();
    }

}
