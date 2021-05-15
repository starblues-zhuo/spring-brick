package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.config.SpringBootLogConfig;
import com.gitee.starblues.extension.log.log4j.Log4jLogRegistry;
import com.gitee.starblues.extension.log.logback.LogbackLogRegistry;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ResourceUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
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

    private final LogRegistry logRegistry;

    public PluginLogConfigProcessor(SpringBootLogExtension.Type type){
        if(type == SpringBootLogExtension.Type.LOG4J){
            logRegistry = new Log4jLogRegistry();
        } else if(type == SpringBootLogExtension.Type.LOGBACK){
            logRegistry = new LogbackLogRegistry();
        } else {
            logRegistry = null;
        }
    }

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
        if (logRegistry == null) {
            return;
        }
        List<Resource> pluginResources = getLogConfigFile(pluginRegistryInfo);
        if (pluginResources == null || pluginResources.isEmpty()) {
            return;
        }
        logRegistry.registry(pluginResources, pluginRegistryInfo);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if (logRegistry == null) {
            return;
        }
        logRegistry.unRegistry(pluginRegistryInfo);
    }

    /**
     * 加载日志配置文件资源
     * @param pluginRegistryInfo 当前插件注册的信息
     * @throws IOException 获取不到配置文件异常
     **/
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
