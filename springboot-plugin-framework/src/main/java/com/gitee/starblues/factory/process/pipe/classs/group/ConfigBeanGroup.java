package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.ConfigBean;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * 对接口ConfigBean实现的类分组
 * @see ConfigBean
 *
 * @author zhangzhuo
 * @version 2.2.2
 */
public class ConfigBeanGroup implements PluginClassGroup {


    public static final String GROUP_ID = "config_bean";


    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        if(aClass == null){
            return false;
        }
        Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(aClass);
        return allInterfacesForClassAsSet.contains(ConfigBean.class);
    }

}
