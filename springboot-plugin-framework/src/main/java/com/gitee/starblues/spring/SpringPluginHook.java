package com.gitee.starblues.spring;

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

}
