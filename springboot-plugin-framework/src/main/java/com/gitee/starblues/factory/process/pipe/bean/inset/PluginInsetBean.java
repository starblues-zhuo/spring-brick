package com.gitee.starblues.factory.process.pipe.bean.inset;

import com.gitee.starblues.factory.PluginRegistryInfo;

/**
 * @author starBlues
 * @version 2.4.1
 */
public interface PluginInsetBean {

    /**
     * 得到bean名称
     * @return bean 名称
     */
    String getBeanName();

    /**
     * 得到bean对象
     * @param pluginRegistryInfo pluginRegistryInfo
     * @return 对象
     */
    Object getBean(PluginRegistryInfo pluginRegistryInfo);

}
