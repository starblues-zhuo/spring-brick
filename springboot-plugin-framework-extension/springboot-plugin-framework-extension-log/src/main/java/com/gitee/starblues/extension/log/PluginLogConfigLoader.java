package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.config.SpringBootLogConfig;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 日志配置加载者
 * @author sousouki
 * @version 2.4.3
 */
public class PluginLogConfigLoader implements PluginResourceLoader {

    private Logger log = LoggerFactory.getLogger(PluginLogConfigLoader.class);

    public static final String KEY = "SpringBootLogConfigLoader";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public ResourceWrapper load(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if (!(basePlugin instanceof SpringBootLogConfig)) {
            log.warn("Plugin '{}' not implements SpringBootLogConfig, If you need to use log in the plugin," + "Please implements SpringBootLogConfig interface", basePlugin.getWrapper().getPluginId());
            return null;
        }
        SpringBootLogConfig springBootLogConfig = (SpringBootLogConfig) basePlugin;
        Set<String> logConfigLocations = springBootLogConfig.logConfigLocations();
        if (logConfigLocations == null || logConfigLocations.isEmpty()) {
            log.warn("SpringBootLogConfig -> logConfigLocations return is empty, " + "Please check configuration");
            return new ResourceWrapper();
        }
        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(pluginRegistryInfo.getPluginClassLoader());
        List<Resource> resources = new ArrayList<>();
        for (String logConfigLocation : logConfigLocations) {
            String matchLocation = ResourceUtils.getMatchLocation(logConfigLocation);
            if (matchLocation == null || "".equals(matchLocation)) {
                continue;
            }
            Resource[] logConfigResources = resourcePatternResolver.getResources(matchLocation);
            if (logConfigResources.length != 0) {
                resources.addAll(Arrays.asList(logConfigResources));
            }
        }
        ResourceWrapper resourceWrapper = new ResourceWrapper();
        resourceWrapper.addResources(resources);
        return resourceWrapper;
    }

    @Override
    public void unload(PluginRegistryInfo pluginRegistryInfo, ResourceWrapper resourceWrapper) throws Exception {

    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }


}
