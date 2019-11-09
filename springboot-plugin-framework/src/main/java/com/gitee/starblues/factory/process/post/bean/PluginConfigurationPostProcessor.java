package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Objects;

/**
 * 插件中Configuration处理者
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginConfigurationPostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
;
    private final GenericApplicationContext applicationContext;

    public PluginConfigurationPostProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        ConfigurationClassPostProcessor configurationClassPostProcessor =
                applicationContext.getBean(ConfigurationClassPostProcessor.class);
        configurationClassPostProcessor.processConfigBeanDefinitions(applicationContext);
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {

    }

}
