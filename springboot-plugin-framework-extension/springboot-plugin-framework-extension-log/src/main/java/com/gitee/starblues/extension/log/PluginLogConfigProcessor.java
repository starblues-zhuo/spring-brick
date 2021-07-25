package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.log4j.Log4jLogRegistry;
import com.gitee.starblues.extension.log.logback.LogbackLogRegistry;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口处理者
 * @author sousouki
 * @version 2.4.3
 */
class PluginLogConfigProcessor implements PluginPipeProcessorExtend {

    private final static Logger LOG = LoggerFactory.getLogger(PluginLogConfigProcessor.class);
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
        Resource resource = getLogConfigFile(pluginRegistryInfo);
        List<Resource> resources = new ArrayList<>(1);
        resources.add(resource);
        logRegistry.registry(resources, pluginRegistryInfo);
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
     *      文件路径配置为 <p>file:D://log.xml<p> <br>
     *      resources路径配置为 <p>classpath:log.xml<p> <br>
     * @param pluginRegistryInfo 当前插件注册的信息
     * @throws IOException 获取不到配置文件异常
     **/
    private Resource getLogConfigFile(PluginRegistryInfo pluginRegistryInfo) throws IOException {
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        String logConfigLocation = pluginApplicationContext.getEnvironment()
                .getProperty(PropertyKey.LOG_CONFIG_LOCATION);
        if (ObjectUtils.isEmpty(logConfigLocation)) {
            return null;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        String matchLocation = ResourceUtils.getMatchLocation(logConfigLocation);
        if (matchLocation == null || "".equals(matchLocation)) {
            LOG.warn("Plugin '{}' not match {}: {}", pluginId, PropertyKey.LOG_CONFIG_LOCATION,
                    logConfigLocation);
            return null;
        }
        if(ResourceUtils.isFile(logConfigLocation)){
            String absolutePath = ResourceUtils.getAbsolutePath(pluginRegistryInfo, matchLocation);
            return new FileSystemResource(absolutePath);
        } else {
            return new ClassPathResource(matchLocation, pluginRegistryInfo.getPluginClassLoader());
        }
    }

}
