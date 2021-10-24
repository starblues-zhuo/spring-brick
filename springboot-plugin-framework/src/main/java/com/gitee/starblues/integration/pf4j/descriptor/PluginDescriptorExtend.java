package com.gitee.starblues.integration.pf4j.descriptor;

import org.pf4j.PluginDescriptor;

/**
 * 扩展 PluginDescriptor 的功能
 * @author starBlues
 * @version 2.4.5
 */
public interface PluginDescriptorExtend extends PluginDescriptor {


    /**
     * 获取配置文件名称
     * @return 文件名称
     */
    String getConfigFileName();

    /**
     * 配置文件Profile
     * @return Profile
     */
    String getConfigFileProfile();

}
