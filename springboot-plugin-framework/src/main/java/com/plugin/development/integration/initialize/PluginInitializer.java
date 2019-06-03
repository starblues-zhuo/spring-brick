package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;

/**
 * @Description: 初始化者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 09:54
 * @Update Date Time:
 * @see
 */
public interface PluginInitializer {

    /**
     * 初始化
     * @throws PluginPlugException 插件安装异常
     */
    void initialize() throws PluginPlugException;

}
