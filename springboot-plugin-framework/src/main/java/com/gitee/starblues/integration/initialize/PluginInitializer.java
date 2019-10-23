package com.gitee.starblues.integration.initialize;


/**
 * 插件初始化者
 * @author zhangzhuo
 * @version 1.0
 * @see AbstractPluginInitializer
 */
public interface PluginInitializer {

    /**
     * 初始化
     * @throws Exception 插件安装异常
     */
    void initialize() throws Exception;


}
