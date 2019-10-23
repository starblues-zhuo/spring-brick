package com.gitee.starblues.integration.initialize;

import com.gitee.starblues.integration.listener.PluginInitializerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的插件初始化者
 * @author zhangzhuo
 * @version 1.0
 * @see AutoPluginInitializer
 * @see ManualPluginInitializer
 */
public abstract class AbstractPluginInitializer implements PluginInitializer{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected PluginInitializerListener pluginInitializerListener;

    protected AbstractPluginInitializer() {
    }

    protected AbstractPluginInitializer(PluginInitializerListener pluginInitializerListener) {
        this.pluginInitializerListener = pluginInitializerListener;
    }

    @Override
    public void initialize() throws Exception {
        log.info("Start execute plugin initializer.");
        this.executeInitialize();
    }

    /**
     * 执行初始化
     * @throws Exception 插件执行初始化异常
     */
    public abstract void executeInitialize()  throws Exception;


    /**
     * 设置监听者
     * @param pluginInitializerListener 初始化监听者
     */
    public void setPluginInitializerListener(PluginInitializerListener pluginInitializerListener) {
        this.pluginInitializerListener = pluginInitializerListener;
    }

}
