package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigBeanGroup;
import com.gitee.starblues.realize.ConfigBean;

import java.util.*;

/**
 * 插件中实现 ConfigBean 接口的的处理者
 * @see ConfigBean
 *
 * @author starBlues
 * @version 2.4.0
 */
public class ConfigBeanRegistrar implements PluginBeanRegistrar {

    public ConfigBeanRegistrar() {
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> configBeans =
                pluginRegistryInfo.getGroupClasses(ConfigBeanGroup.GROUP_ID);
        if(configBeans == null || configBeans.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        for (Class<?> aClass : configBeans) {
            if(aClass == null){
                continue;
            }
            springBeanRegister.register(pluginId, aClass);
        }
    }


}
