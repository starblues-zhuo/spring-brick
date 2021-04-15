package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.config.SpringBootLogConfig;
import com.gitee.starblues.extension.log.util.LogConfigProcess;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ResourceUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 接口处理者
 * @author sousouki
 * @version 2.4.3
 */
class PluginLogConfigProcessor implements PluginPipeProcessorExtend {

    private Logger log = LoggerFactory.getLogger(PluginLogConfigProcessor.class);

    private final LogConfigProcess logConfigProcess = LogConfigProcess.getInstance();

    @Override
    public String key() {
        return "SpringBootLogConfigProcessor";
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
        List<Resource> pluginResources = getLogConfigFile(pluginRegistryInfo);
        if (pluginResources == null || pluginResources.isEmpty()) {
            return;
        }
        logConfigProcess.loadLogConfig(pluginResources, pluginRegistryInfo.getPluginWrapper());
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        logConfigProcess.unloadLogConfig(pluginRegistryInfo.getPluginWrapper());
    }

    private List<Resource> getLogConfigFile(PluginRegistryInfo pluginRegistryInfo) throws IOException {
        SpringBootLogConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                pluginRegistryInfo.getConfigSingletons(),
                SpringBootLogConfig.class);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();

        if(config == null){
            log.warn("Not found 'SpringBootLogConfig' in plugin[{}]. " +
                    "If you need to use log in the plugin, " +
                    "Please implements 'SpringBootLogConfig' interface", pluginId);
            return null;
        }
        Set<String> logConfigLocations = config.logConfigLocations();
        if (logConfigLocations == null || logConfigLocations.isEmpty()) {
            log.warn("SpringBootLogConfig -> logConfigLocations return is empty in plugin[{}], " +
                    "Please check configuration", pluginId);
            return null;
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
        return resources;
    }

}
