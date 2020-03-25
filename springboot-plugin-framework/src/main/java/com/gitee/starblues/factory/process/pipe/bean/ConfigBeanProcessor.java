package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigBeanGroup;
import com.gitee.starblues.realize.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * 插件中实现 ConfigBean 接口的的处理者
 * @see ConfigBean
 *
 * @author zhangzhuo
 * @version 2.2.2
 */
public class ConfigBeanProcessor implements PluginPipeProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String KEY = "ConfigBeanProcessor";

    private final SpringBeanRegister springBeanRegister;
    private final ApplicationContext applicationContext;

    public ConfigBeanProcessor(ApplicationContext applicationContext) {
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
        this.applicationContext = applicationContext;
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> configBeans =
                pluginRegistryInfo.getGroupClasses(ConfigBeanGroup.GROUP_ID);
        if(configBeans == null || configBeans.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        Map<String, ConfigBean> configBeanMap = new HashMap<>();
        for (Class<?> aClass : configBeans) {
            if(aClass == null){
                continue;
            }
            String name = springBeanRegister.register(pluginId, aClass);
            Object bean = applicationContext.getBean(name);
            if(bean instanceof ConfigBean){
                ConfigBean configBean = (ConfigBean) bean;
                configBean.initialize();
                configBeanMap.put(name, configBean);
            }
        }
        pluginRegistryInfo.addProcessorInfo(KEY, configBeanMap);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Map<String, ConfigBean> configBeanMap = pluginRegistryInfo.getProcessorInfo(KEY);
        if(configBeanMap == null){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        configBeanMap.forEach((beanName, configBean)->{
            if(configBean == null){
                return;
            }
            try {
                configBean.destroy();
            } catch (Exception e) {
                log.error("ConfigBean '' destroy exception. {}", e.getMessage(), e);
            }
            springBeanRegister.unregister(pluginId, beanName);
        });

    }


}
