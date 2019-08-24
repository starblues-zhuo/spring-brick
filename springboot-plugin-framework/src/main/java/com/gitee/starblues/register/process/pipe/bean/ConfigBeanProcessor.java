package com.gitee.starblues.register.process.pipe.bean;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.exception.ConfigurationParseException;
import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.register.PluginInfoContainer;
import com.gitee.starblues.register.PluginRegistryInfo;
import com.gitee.starblues.register.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.register.process.pipe.bean.configuration.ConfigurationParser;
import com.gitee.starblues.register.process.pipe.bean.configuration.PluginConfigDefinition;
import com.gitee.starblues.register.process.pipe.bean.configuration.YamlConfigurationParser;
import com.gitee.starblues.register.process.pipe.classs.PluginClassProcess;
import com.gitee.starblues.register.process.pipe.classs.group.ConfigDefinitionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件Controller bean注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class ConfigBeanProcessor implements PluginPipeProcessor {

    private static final String KEY = "ConfigBeanProcessor";

    private final static Logger LOG = LoggerFactory.getLogger(ConfigBeanProcessor.class);
    private final ConfigurationParser configurationParser;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public ConfigBeanProcessor(ApplicationContext mainApplicationContext) {
        IntegrationConfiguration integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
        this.defaultListableBeanFactory = (DefaultListableBeanFactory)
                mainApplicationContext.getAutowireCapableBeanFactory();
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> configDefinitions =
                pluginRegistryInfo.getGroupClasses(ConfigDefinitionGroup.CONFIG_DEFINITION);
        if(configDefinitions == null || configDefinitions.isEmpty()){
            return;
        }
        Set<String> beanNames = new HashSet<>();
        for (Class<?> aClass : configDefinitions) {
            String beanName = registry(pluginRegistryInfo, aClass);
            if(!StringUtils.isEmpty(beanName)){
                beanNames.add(beanName);
                PluginInfoContainer.addRegisterBeanName(beanName);
            }
        }
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = pluginRegistryInfo.getProcessorInfo(KEY);
        if(beanNames == null){
            return;
        }
        for (String beanName : beanNames) {
            if(defaultListableBeanFactory.containsSingleton(beanName)){
                defaultListableBeanFactory.destroySingleton(beanName);
                PluginInfoContainer.removeRegisterBeanName(beanName);
            }
        }
    }

    /**
     * 注册配置文件
     * @param pluginRegistryInfo 插件注册的信息
     * @param aClass 配置文件类
     * @return 注册的bean名称
     * @throws PluginBeanFactoryException
     */
    private String registry(PluginRegistryInfo pluginRegistryInfo, Class<?> aClass) throws Exception{
        ConfigDefinition configDefinition = aClass.getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return null;
        }
        String fileName = configDefinition.value();
        if(StringUtils.isEmpty(fileName)){
            throw new Exception(aClass.getName() + " configDefinition value is null");
        }
        try {
            PluginConfigDefinition pluginConfigDefinition =
                    new PluginConfigDefinition(fileName, aClass);
            Object parseObject = configurationParser.parse(pluginRegistryInfo.getBasePlugin(), pluginConfigDefinition);
            String name = configDefinition.name();
            if(StringUtils.isEmpty(name)){
                name = aClass.getName();
            }
            if(!defaultListableBeanFactory.containsSingleton(name)){
                defaultListableBeanFactory.registerSingleton(name, parseObject);
            }
            return name;
        } catch (ConfigurationParseException e) {
            e.printStackTrace();
            String errorMsg = "parse config <" + aClass.getName() + "> error,errorMsg : " + e.getMessage();
            throw new Exception(errorMsg, e);
        }
    }

}
