package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.bean.configuration.ConfigurationParser;
import com.gitee.starblues.factory.process.pipe.bean.configuration.PluginConfigDefinition;
import com.gitee.starblues.factory.process.pipe.bean.configuration.YamlConfigurationParser;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigDefinitionGroup;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.realize.ConfigDefinitionTip;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.PluginConfigUtils;
import org.pf4j.util.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 插件中配置文件 bean 的处理者。包括配置文件
 * @author starBlues
 * @version 2.4.0
 */
public class ConfigFileBeanRegistrar implements PluginBeanRegistrar {

    private final ConfigurationParser configurationParser;
    private final IntegrationConfiguration integrationConfiguration;

    public ConfigFileBeanRegistrar(ApplicationContext mainApplicationContext) {
        integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> configDefinitions =
                pluginRegistryInfo.getGroupClasses(ConfigDefinitionGroup.GROUP_ID);
        if(configDefinitions == null || configDefinitions.isEmpty()){
            return;
        }
        for (Class<?> aClass : configDefinitions) {
            registry(pluginRegistryInfo, aClass);
        }
    }

    /**
     * 注册配置文件
     * @param pluginRegistryInfo 插件注册的信息
     * @param aClass 配置文件类
     * @throws Exception Exception
     */
    private void registry(PluginRegistryInfo pluginRegistryInfo, Class<?> aClass) throws Exception{
        ConfigDefinition configDefinition = aClass.getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return;
        }
        PluginConfigUtils.FileNamePack fileNamePack = PluginConfigUtils.getConfigFileName(
                configDefinition.fileName(),
                configDefinition.prodSuffix(),
                configDefinition.devSuffix(),
                integrationConfiguration.environment());
        String fileName = PluginConfigUtils.joinConfigFileName(fileNamePack);
        Object parseObject = null;
        if(!StringUtils.isNullOrEmpty(fileName)){
            PluginConfigDefinition pluginConfigDefinition =
                    new PluginConfigDefinition(fileName, aClass);
            parseObject = configurationParser.parse(pluginRegistryInfo,
                    pluginConfigDefinition);
        } else {
            parseObject = aClass.newInstance();
        }

        String name = aClass.getName();
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        setConfigDefinitionTip(pluginRegistryInfo, parseObject);
        springBeanRegister.registerSingleton(name, parseObject);
        pluginRegistryInfo.addConfigSingleton(parseObject);
    }

    /**
     * 设置小工具类
     * @param parseObject 当前的配置对象
     */
    private void setConfigDefinitionTip(PluginRegistryInfo pluginRegistryInfo, Object parseObject) {
        Class<?> aClass = parseObject.getClass();
        List<Field> fields = ClassUtils.getAllFields(aClass);
        ConfigDefinitionTip configDefinitionTip = new ConfigDefinitionTip(pluginRegistryInfo);
        for (Field field : fields) {
            if(field.getType() == ConfigDefinitionTip.class){
                field.setAccessible(true);
                ReflectionUtils.setField(field, parseObject, configDefinitionTip);
            }
        }
    }

}
