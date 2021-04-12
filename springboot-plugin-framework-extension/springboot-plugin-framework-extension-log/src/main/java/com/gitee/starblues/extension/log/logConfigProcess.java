package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.util.LogConfigProcess;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.utils.OrderPriority;
import org.pf4j.PluginWrapper;
import org.springframework.core.io.Resource;

import java.util.List;

public class PluginLogConfigProcessor implements PluginPipeProcessorExtend {

    private final LogConfigProcess logConfigProcess = LogConfigProcess.getInstance();

    @Override
    public String key() {
        return null;
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getLowPriority();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if (logConfigProcess == null) {
            return;
        }
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        ResourceWrapper resourceWrapper = pluginRegistryInfo.getPluginLoadResource(PluginLogConfigLoader.KEY);
        if (resourceWrapper == null) {
            return;
        }

        List<Resource> pluginResources = resourceWrapper.getResources();
        if (pluginResources == null || pluginResources.isEmpty()) {
            return;
        }
        logConfigProcess.loadLogConfig(pluginResources, pluginWrapper);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        logConfigProcess.unloadLogConfig(pluginRegistryInfo.getPluginWrapper());
    }
}
