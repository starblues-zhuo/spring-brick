package com.gitee.starblues.spring;


import com.gitee.starblues.core.exception.PluginProhibitStopException;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;

/**
 * 插件把柄接口
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginHook extends AutoCloseable{

    /**
     * 停止前校验. 如果抛出 PluginProhibitStopException 异常, 表示当前插件不可停止
     * @throws PluginProhibitStopException 插件禁止停止
     */
    void stopVerify() throws PluginProhibitStopException;

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
