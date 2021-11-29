package com.gitee.starblues.spring;

import org.springframework.context.support.GenericApplicationContext;

/**
 * 插件 SpringApplication
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginSpringApplication {


    /**
     * 运行
     * @return GenericApplicationContext
     */
    GenericApplicationContext run();

    /**
     * 关闭
     */
    void close();


    /**
     * 得到 ApplicationContext
     * @return GenericApplicationContext
     */
    GenericApplicationContext getApplicationContext();


}
