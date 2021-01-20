package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.PluginInfoContainer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.bean.configuration.ConfigurationParser;
import com.gitee.starblues.factory.process.pipe.bean.configuration.PluginConfigDefinition;
import com.gitee.starblues.factory.process.pipe.bean.configuration.YamlConfigurationParser;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigDefinitionGroup;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件中配置文件 bean 的处理者。包括配置文件
 * @author starBlues
 * @version 2.4.0
 */
public class ConfigFileBeanProcessor implements PluginPipeProcessor {

    private static final String KEY = "ConfigFileBeanProcessor";

    private final ConfigurationParser configurationParser;
    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final IntegrationConfiguration integrationConfiguration;

    public ConfigFileBeanProcessor(ApplicationContext mainApplicationContext) {
        integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
        this.defaultListableBeanFactory = (DefaultListableBeanFactory)
                mainApplicationContext.getAutowireCapableBeanFactory();
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> configDefinitions =
                pluginRegistryInfo.getGroupClasses(ConfigDefinitionGroup.GROUP_ID);
        if(configDefinitions == null || configDefinitions.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        Set<String> beanNames = new HashSet<>();
        for (Class<?> aClass : configDefinitions) {
            String beanName = registry(pluginRegistryInfo, aClass);
            if(!StringUtils.isEmpty(beanName)){
                beanNames.add(beanName);
                PluginInfoContainer.addRegisterBeanName(pluginId, beanName);
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
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (String beanName : beanNames) {
            if(defaultListableBeanFactory.containsSingleton(beanName)){
                defaultListableBeanFactory.destroySingleton(beanName);
                PluginInfoContainer.removeRegisterBeanName(pluginId, beanName);
            }
        }
    }

    /**
     * 注册配置文件
     * @param pluginRegistryInfo 插件注册的信息
     * @param aClass 配置文件类
     * @return 注册的bean名称
     * @throws Exception Exception
     */
    private String registry(PluginRegistryInfo pluginRegistryInfo, Class<?> aClass) throws Exception{
        ConfigDefinition configDefinition = aClass.getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return null;
        }
        String fileName = getConfigFileName(configDefinition, aClass);
        PluginConfigDefinition pluginConfigDefinition =
                new PluginConfigDefinition(fileName, aClass);
        Object parseObject = configurationParser.parse(pluginRegistryInfo,
                pluginConfigDefinition);
        String name = configDefinition.beanName();
        if(StringUtils.isEmpty(name)){
            name = aClass.getName();
        }
        name = name + "@" + pluginRegistryInfo.getPluginWrapper().getPluginId();
        if(!defaultListableBeanFactory.containsSingleton(name)){
            defaultListableBeanFactory.registerSingleton(name, parseObject);
        }
        return name;
    }

    /**
     * 根据项目运行环境模式来获取配置文件
     * @param configDefinition 配置的注解
     * @param aClass 当前配置类
     * @return 文件名称
     */
    private String getConfigFileName(ConfigDefinition configDefinition, Class<?> aClass){
        String fileName = configDefinition.value();
        if(StringUtils.isEmpty(fileName)){
            throw new IllegalArgumentException(aClass.getName() + " configDefinition value is null");
        }

        String fileNamePrefix;
        String fileNamePrefixSuffix;

        if(fileName.lastIndexOf(".") == -1) {
            fileNamePrefix = fileName;
            fileNamePrefixSuffix = "";
        } else {
            int index = fileName.lastIndexOf(".");
            fileNamePrefix = fileName.substring(0, index);
            fileNamePrefixSuffix = fileName.substring(index);
        }

        RuntimeMode environment = integrationConfiguration.environment();
        if(environment == RuntimeMode.DEPLOYMENT){
            // 生产环境
            fileNamePrefix = fileNamePrefix + configDefinition.prodSuffix();
        } else if(environment == RuntimeMode.DEVELOPMENT){
            // 开发环境
            fileNamePrefix = fileNamePrefix + configDefinition.devSuffix();
        }
        return fileNamePrefix + fileNamePrefixSuffix;
    }

}
