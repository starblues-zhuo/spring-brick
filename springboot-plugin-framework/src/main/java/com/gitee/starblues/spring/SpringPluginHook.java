package com.gitee.starblues.spring;


import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;

/**
 * 插件把柄接口
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginHook extends AutoCloseable{

    /**
     * 返回插件 ApplicationContext
     * @return ApplicationContext
     */
    ApplicationContext getApplicationContext();

    /**
     * 得到插件中对 web 的配置
     * @return WebConfig
     */
    WebConfig getWebConfig();

    /**
     * 获取插件中对 Thymeleaf 的配置
     * @return ThymeleafConfig
     */
    ThymeleafConfig getThymeleafConfig();

}
