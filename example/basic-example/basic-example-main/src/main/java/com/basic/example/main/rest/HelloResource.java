package com.basic.example.main.rest;

import com.basic.example.main.plugin.Hello;
import com.gitee.starblues.integration.user.PluginUser;
import com.basic.example.main.plugin.ConsoleName;
import com.basic.example.main.plugin.ConsoleNameFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接口演示
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping(path = "/hello")
public class HelloResource {


    private final PluginUser pluginUser;
    private final ConsoleNameFactory consoleNameFactory;

    public HelloResource(PluginUser pluginUser,
                         ConsoleNameFactory consoleNameFactory) {
        this.pluginUser = pluginUser;
        this.consoleNameFactory = consoleNameFactory;
    }

    @GetMapping
    public String sya(){
        return "hello spring boot plugin example";
    }


    /**
     * 通过 PluginUser 获取到主程序的实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的实现类的 name() 方法的输出
     */
    @GetMapping("/mainConsoleName")
    public String mainConsoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        // 获取到实现该接口的实现类
        List<ConsoleName> consoleNames = pluginUser.getMainBeans(ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }


    /**
     * 通过 PluginUser 获取到主程序和插件中所有的实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的实现类的 name() 方法的输出
     */
    @GetMapping("/consoleName")
    public String consoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        // 获取到实现该接口的实现类
        List<ConsoleName> consoleNames = pluginUser.getBeans(ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }

    /**
     * 通过 PluginUser 获取插件中的实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 接口的插件中的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的插件中实现类的 name() 方法的输出
     */
    @GetMapping("/pluginConsoleName")
    public String pluginConsoleName(){
        StringBuffer stringBuffer = new StringBuffer();
        // 获取到插件中实现该接口的实现类
        List<ConsoleName> consoleNames = pluginUser.getPluginBeans(ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }

    /**
     * 通过 AbstractPluginSpringBeanRefresh 工厂获取实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的实现类的 name() 方法的输出
     */
    @GetMapping("/pluginConsoleName2")
    public String pluginConsoleName2(){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = consoleNameFactory.getBeans();
        return getConsoleNames(stringBuffer, consoleNames);
    }


    /**
     * 通过 插件id 获取指定的插件中的实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的实现类的 name() 方法的输出
     */
    @GetMapping("/consoleName/{pluginId}")
    public String plugin1ConsoleName(@PathVariable("pluginId") String pluginId){
        StringBuffer stringBuffer = new StringBuffer();
        List<ConsoleName> consoleNames = pluginUser.getPluginBeans(pluginId, ConsoleName.class);
        return getConsoleNames(stringBuffer, consoleNames);
    }


    /**
     * 通过 插件id 获取指定的插件中的实现类
     * 打印实现接口 com.basic.demo.main.main.plugin.ConsoleName 的实现类
     * @return 返回所有实现 com.basic.demo.main.main.plugin.ConsoleName 接口的实现类的 name() 方法的输出
     */
    @GetMapping("/extensions")
    public String hello(){

        List<Hello> hellos = pluginUser.getPluginExtensions(Hello.class);
        if(hellos == null){
            return "Not impl Hello";
        } else {
            StringBuffer stringBuffer = new StringBuffer("extensions impl : <br/>");
            for (Hello hello : hellos) {
                stringBuffer.append(hello.getName())
                        .append("<br/>");
            }
            return stringBuffer.toString();
        }
    }

    /**
     * 调用接口 name() 方法，并拼接输出
     * @param stringBuffer stringBuffer
     * @param consoleNames 所有 ConsoleName 的实现类
     * @return 拼接的字符串
     */
    private String getConsoleNames(StringBuffer stringBuffer, List<ConsoleName> consoleNames) {
        for (ConsoleName consoleName : consoleNames) {
            stringBuffer.append(consoleName.name())
                    .append("<br/>");
        }
        return stringBuffer.toString();
    }

}
