package com.gitee.starblues.bootstrap.processor.web;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorException;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * 插件web资源处理器. 获取资源配置
 * @author starBlues
 * @version 3.0.0
 */
public class PluginStaticResourceProcessor implements SpringPluginProcessor {


    /**
     * 静态文件配置前缀
     * 静态文件路径
     *  classpath: static/
     *  file: D://path/test
     */
    private final static String STATIC_LOCATIONS = "spring.resources.static-locations";


    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String property = environment.getProperty(STATIC_LOCATIONS);
        if(ObjectUtils.isEmpty(property)){
            return;
        }
        String[] staticLocations = property.split(",");
        if (ObjectUtils.isEmpty(staticLocations)) {
            return;
        }
        Set<String> staticLocationsSet = new HashSet<>(staticLocations.length);
        for (String staticLocation : staticLocations) {
            if(ObjectUtils.isEmpty(staticLocation)){
                continue;
            }
            staticLocationsSet.add(staticLocation);
        }
        context.getWebConfig().setResourceLocations(staticLocationsSet);
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }
}
