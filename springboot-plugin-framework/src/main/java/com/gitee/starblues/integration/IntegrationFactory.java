package com.gitee.starblues.integration;

import org.pf4j.PluginException;
import org.pf4j.PluginManager;

/**
 * 集成工厂
 * @author zhangzhuo
 * @version 1.0
 * @see DefaultPluginApplication
 */
public interface IntegrationFactory {

    /**
     * 得到插件管理者
     * @param configuration 插件配置
     * @return 插件管理者
     * @throws PluginException 插件异常
     */
    PluginManager getPluginManager(IntegrationConfiguration configuration) throws PluginException;

}
