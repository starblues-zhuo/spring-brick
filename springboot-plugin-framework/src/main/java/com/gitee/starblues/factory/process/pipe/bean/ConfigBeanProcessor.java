package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigBeanGroup;
import com.gitee.starblues.realize.ConfigBean;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件中实现 ConfigBean 接口的的处理者
 * @see ConfigBean
 *
 * @author zhangzhuo
 * @version 2.2.2
 */
public class ConfigBeanProcessor implements PluginPipeProcessor {

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
        Set<String> beanNames = new HashSet<>();
        for (Class<?> aClass : configBeans) {
            if(aClass == null){
                continue;
            }
            String name = springBeanRegister.register(pluginId, aClass);
            beanNames.add(name);
            Object bean = applicationContext.getBean(name);
            if(bean instanceof ConfigBean){
                ((ConfigBean)bean).initialize();
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
            Object bean = applicationContext.getBean(beanName);
            if(bean instanceof ConfigBean){
                ((ConfigBean)bean).destroy();
            }
        }
    }


}
