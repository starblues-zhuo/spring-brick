package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 抽象的初始化者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-31 10:36
 * @Update Date Time:
 * @see
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
     * @throws PluginPlugException
     */
    public abstract void executeInitialize()  throws PluginPlugException;

}
