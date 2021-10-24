package com.gitee.starblues.integration.pf4j.descriptor;

import org.pf4j.PluginDescriptor;

/**
 * @author zhangzhuo@acoinfo.com
 * @version 1.0
 * @date 2021-10-23
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
