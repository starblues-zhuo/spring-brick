package com.gitee.starblues.spring;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * 插件 SpringApplication
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginSpringApplication {

    /**
     * 运行
     * @return ConfigurableApplicationContext
     */
    ConfigurableApplicationContext run();

    /**
     * 关闭
     */
    void close();


    /**
     * 得到 ApplicationContext
     * @return GenericApplicationContext
     */
    ConfigurableApplicationContext getApplicationContext();


}
