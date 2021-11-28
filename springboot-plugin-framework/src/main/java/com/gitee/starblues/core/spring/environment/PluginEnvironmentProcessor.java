package com.gitee.starblues.core.spring.environment;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件 Environment 处理者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginEnvironmentProcessor extends Ordered {

    /**
     * 处理 Environment
     * @param environment ConfigurableEnvironment
     * @param pnClassLoader ResourceLoader
     */
    void postProcessEnvironment(ConfigurableEnvironment environment, ResourceLoader pnClassLoader);

}
