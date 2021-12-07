package com.gitee.starblues.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件 SpringApplication
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginSpringApplication {

    /**
     * 运行
     * @return ConfigurableApplicationContext
     * @throws Exception Exception
     */
    GenericApplicationContext run() throws Exception;

    /**
     * 关闭
     */
    void close();


    /**
     * 得到 ApplicationContext
     * @return GenericApplicationContext
     */
    GenericApplicationContext getApplicationContext();

    /**
     * 得到 ResourceLoader
     * @return ResourceLoader
     */
    ResourceLoader getResourceLoader();


}
