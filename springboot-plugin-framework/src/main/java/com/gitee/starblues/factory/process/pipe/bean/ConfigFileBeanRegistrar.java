package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.bean.configuration.ConfigurationParser;
import com.gitee.starblues.factory.process.pipe.bean.configuration.PluginConfigDefinition;
import com.gitee.starblues.factory.process.pipe.bean.configuration.YamlConfigurationParser;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigDefinitionGroup;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件中配置文件 bean 的处理者。包括配置文件
 * @author starBlues
 * @version 2.4.0
 */
public class ConfigFileBeanRegistrar implements PluginBeanRegistrar {

    private static final String KEY = "ConfigFileBeanProcessor";

    private final ConfigurationParser configurationParser;
    private final IntegrationConfiguration integrationConfiguration;

    public ConfigFileBeanRegistrar(ApplicationContext mainApplicationContext) {
        integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
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
        Set<String> beanNames = new HashSet<>();
        for (Class<?> aClass : configDefinitions) {
            String beanName = registry(pluginRegistryInfo, aClass);
            if(!StringUtils.isNullOrEmpty(beanName)){
                beanNames.add(beanName);
            }
        }
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
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
        Object parseObject = null;
        if(!StringUtils.isNullOrEmpty(fileName)){
            PluginConfigDefinition pluginConfigDefinition =
                    new PluginConfigDefinition(fileName, aClass);
            parseObject = configurationParser.parse(pluginRegistryInfo,
                    pluginConfigDefinition);
        } else {
            parseObject = aClass.newInstance();
        }

        String name = configDefinition.beanName();
        if(StringUtils.isNullOrEmpty(name)){
            name = aClass.getName();
        }
        name = name + "@" + pluginRegistryInfo.getPluginWrapper().getPluginId();
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        springBeanRegister.registerSingleton(name, parseObject);
        pluginRegistryInfo.addConfigSingleton(parseObject);
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
        if(StringUtils.isNullOrEmpty(fileName)){
            return fileName;
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
