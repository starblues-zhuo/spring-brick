package com.gitee.starblues.realize;

import com.gitee.starblues.factory.process.pipe.PluginAutoConfigurationInstaller;

/**
 * 插件自动装配选择者, 装配spring-boot-xx-starter
 * @author starBlues
 * @version 2.4.3
 */
public interface AutoConfigurationSelector {

    /**
     * 选择插件所需集成的AutoConfiguration
     * @param installer 自动装配安装者
     */
    void select(PluginAutoConfigurationInstaller installer);

}
