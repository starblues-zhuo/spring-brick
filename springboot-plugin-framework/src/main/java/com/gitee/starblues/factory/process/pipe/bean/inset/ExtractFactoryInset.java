package com.gitee.starblues.factory.process.pipe.bean.inset;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.extract.ExtractFactory;

/**
 * ExtractFactory 扩展工厂注册者
 * @author starBlues
 * @version 2.4.1
 */
public class ExtractFactoryInset implements PluginInsetBean{

    @Override
    public String getBeanName() {
        return ExtractFactoryInset.class.getName();
    }

    @Override
    public Object getBean(PluginRegistryInfo pluginRegistryInfo) {
        return ExtractFactory.getInstant();
    }
}
