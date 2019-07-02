package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.ConfigurationParseException;
import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.factory.bean.register.configuration.ConfigurationParser;
import com.gitee.starblues.factory.bean.register.configuration.PluginConfigDefinition;
import com.gitee.starblues.factory.bean.register.configuration.YamlConfigurationParser;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件Controller bean注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class PluginConfigBeanRegister extends AbstractPluginBeanRegister<String>{

    private final static Logger LOG = LoggerFactory.getLogger(PluginConfigBeanRegister.class);
    private final ConfigurationParser configurationParser;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public PluginConfigBeanRegister(ApplicationContext mainApplicationContext) throws PluginBeanFactoryException {
        super(mainApplicationContext);
        IntegrationConfiguration integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.configurationParser = new YamlConfigurationParser(integrationConfiguration);
        this.defaultListableBeanFactory = (DefaultListableBeanFactory)
                mainApplicationContext.getAutowireCapableBeanFactory();
    }


    @Override
    public String key() {
        return "PluginConfigBeanRegister";
    }

    @Override
    public String registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException {
        ConfigDefinition configDefinition = aClass.getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return null;
        }
        String fileName = configDefinition.value();
        if(StringUtils.isEmpty(fileName)){
            throw new PluginBeanFactoryException(aClass.getName() + " configDefinition value is null");
        }
        try {
            PluginConfigDefinition pluginConfigDefinition =
                    new PluginConfigDefinition(fileName, aClass);
            Object parseObject = configurationParser.parse(basePlugin, pluginConfigDefinition);
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
            throw new PluginBeanFactoryException(errorMsg, e);
        }
    }

    @Override
    public void unRegistry(BasePlugin basePlugin, String beanName) throws PluginBeanFactoryException {
        if(defaultListableBeanFactory.containsSingleton(beanName)){
            defaultListableBeanFactory.destroySingleton(beanName);
        }
    }

    @Override
    public int order() {
        return OrderExecution.MIDDLE + 10;
    }


}
