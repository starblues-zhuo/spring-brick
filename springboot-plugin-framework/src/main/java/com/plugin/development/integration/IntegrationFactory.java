package com.plugin.development.integration;

import org.pf4j.PluginException;
import org.pf4j.PluginManager;

/**
 * @Description: 集成工程.
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-26 19:33
 * @Update Date Time:
 * @see
 */
public interface IntegrationFactory {

    /**
     * 得到插件管理者
     * @param configuration
     * @return
     * @throws PluginException
     */
    PluginManager getPluginManager(IntegrationConfiguration configuration) throws PluginException;

}
