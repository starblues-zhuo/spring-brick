package com.gitee.starblues.factory.process.pipe.bean.inset;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.realize.PluginUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * PluginUtils 对象注册者
 * @author starBlues
 * @version 2.4.1
 */
public class PluginUtilsInset implements PluginInsetBean{
    @Override
    public String getBeanName() {
        return "PluginUtilsName";
    }

    @Override
    public Object getBean(PluginRegistryInfo pluginRegistryInfo) {
        GenericApplicationContext parentApplicationContext = pluginRegistryInfo.getMainApplicationContext();
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        return new PluginUtils(parentApplicationContext,
                pluginApplicationContext,
                pluginRegistryInfo.getPluginWrapper().getDescriptor());
    }
}
