package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.PluginResourceStorage;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.web.PluginStaticResourceResolver;

/**
 * 默认的插件启动介入者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginLaunchInvolved implements PluginLaunchInvolved{

    @Override
    public void before(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        PluginResourceStorage.addPlugin(descriptor);
    }

    @Override
    public void after(InsidePluginDescriptor descriptor, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        PluginStaticResourceResolver.parse(descriptor, classLoader, pluginHook.getWebConfig());
    }

    @Override
    public void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        String pluginId = descriptor.getPluginId();
        PluginResourceStorage.removePlugin(pluginId);
        PluginStaticResourceResolver.remove(pluginId);
    }
}
