package com.plugin.development.context.process;

import com.plugin.development.annotation.ConfigDefinition;
import com.plugin.development.exception.ConfigurationParseException;
import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.integration.IntegrationConfiguration;
import com.plugin.development.context.configuration.ConfigurationParser;
import com.plugin.development.context.configuration.YamlConfigurationParser;
import com.plugin.development.context.configuration.PluginConfigDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 插件配置处理者
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginConfigProcess implements PluginPostBeanProcess{

    private ConfigurationParser configurationParser;

    public PluginConfigProcess(ApplicationContext mainApplicationContext){
        IntegrationConfiguration integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
    }


    @Override
    public void process(Object bean, AnnotationConfigApplicationContext pluginApplicationContext) throws PluginBeanFactoryException {
        if(bean == null){
            return;
        }
        Class<?> aClass = bean.getClass();
        ConfigDefinition configDefinition = aClass.getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return;
        }
        String fileName = configDefinition.value();
        if(StringUtils.isEmpty(fileName)){
            throw new PluginBeanFactoryException(aClass.getName() + " configDefinition value is null");
        }
        try {
            Object parseObject = configurationParser.parse(new PluginConfigDefinition(fileName, aClass));
            BeanUtils.copyProperties(parseObject, bean);
        } catch (ConfigurationParseException e) {
            String errorMsg = "parse config <" + aClass.getName() + "> error,errorMsg : " + e.getMessage();
            throw new PluginBeanFactoryException(errorMsg, e);
        }
    }

    /**
     * 在此处可以更换插件配置文件解析者
     * @param configurationParser 插件配置文件解析者
     */
    public void setConfigurationParser(ConfigurationParser configurationParser) {
        this.configurationParser = configurationParser;
    }
}
