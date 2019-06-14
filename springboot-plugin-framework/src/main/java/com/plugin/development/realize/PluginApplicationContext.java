package com.plugin.development.realize;

import org.springframework.context.ApplicationContext;

/**
 * 插件的 ApplicationContext
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginApplicationContext {

    /**
     * 得到 applicationContext
     * @return Spring上下文
     */
    ApplicationContext getApplicationContext();

}
