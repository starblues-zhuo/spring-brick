package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的插件初始化者
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.integration.initialize.AutoPluginInitializer
 * @see com.plugin.development.integration.initialize.ManualPluginInitializer
 */
public abstract class AbstractPluginInitializer implements PluginInitializer{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize() throws PluginPlugException {
        log.info("Start execute plugin initializer.");
        this.executeInitialize();
    }

    /**
     * 执行初始化
     * @throws PluginPlugException 插件插拔异常
     */
    public abstract void executeInitialize()  throws PluginPlugException;

}
