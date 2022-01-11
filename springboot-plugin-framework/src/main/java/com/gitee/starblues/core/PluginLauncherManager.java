package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.plugin.DefaultPluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginLauncher;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.MainApplicationContextProxy;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.invoke.DefaultInvokeSupperCache;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginLauncherManager extends DefaultPluginManager{

    private final Map<String, RegistryPluginInfo> pluginRegistryInfoMap = new ConcurrentHashMap<>();

    private final MainApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;
    private final InvokeSupperCache invokeSupperCache;


    public PluginLauncherManager(RealizeProvider realizeProvider,
                                 GenericApplicationContext applicationContext,
                                 IntegrationConfiguration configuration) {
        super(realizeProvider, configuration.pluginPath());
        this.mainApplicationContext = new MainApplicationContextProxy(
                applicationContext.getBeanFactory(),
                applicationContext);
        this.configuration = configuration;
        this.invokeSupperCache = new DefaultInvokeSupperCache();
    }


    @Override
    protected void start(PluginWrapperInside pluginWrapper) throws Exception {
        super.start(pluginWrapper);
        PluginDescriptor pluginDescriptor = pluginWrapper.getPluginDescriptor();
        PluginInteractive pluginInteractive = new DefaultPluginInteractive(pluginDescriptor,
                mainApplicationContext, configuration, invokeSupperCache);
        PluginLauncher pluginLauncher = new PluginLauncher(pluginInteractive);
        SpringPluginHook springPluginHook = pluginLauncher.run();
        RegistryPluginInfo registryPluginInfo = new RegistryPluginInfo(pluginDescriptor, springPluginHook);
        pluginRegistryInfoMap.put(pluginDescriptor.getPluginId(), registryPluginInfo);
    }


    @Override
    protected void stop(PluginWrapperInside pluginWrapper) throws Exception {
        String pluginId = pluginWrapper.getPluginId();
        RegistryPluginInfo registryPluginInfo = pluginRegistryInfoMap.get(pluginId);
        if(registryPluginInfo == null){
            throw new PluginException("没有发现插件 '" + pluginId +  "' 信息");
        }
        registryPluginInfo.getSpringPluginHook().close();
        invokeSupperCache.remove(pluginId);
        pluginRegistryInfoMap.remove(pluginId);
        super.stop(pluginWrapper);
    }

    private static class RegistryPluginInfo{
        private final PluginDescriptor descriptor;
        private final SpringPluginHook springPluginHook;

        private RegistryPluginInfo(PluginDescriptor descriptor, SpringPluginHook springPluginHook) {
            this.descriptor = descriptor;
            this.springPluginHook = springPluginHook;
        }

        public PluginDescriptor getDescriptor() {
            return descriptor;
        }

        public SpringPluginHook getSpringPluginHook() {
            return springPluginHook;
        }
    }
}
