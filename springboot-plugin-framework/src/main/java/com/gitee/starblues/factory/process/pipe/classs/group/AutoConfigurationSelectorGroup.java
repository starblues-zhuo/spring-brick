package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.AutoConfigurationSelector;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * 自动装配分组者
 *
 * @author starBlues
 * @version 2.1.0
 */
public class AutoConfigurationSelectorGroup implements PluginClassGroup {

    public static final String ID = "AutoConfigurationSelectorGroup";

    @Override
    public String groupId() {
        return ID;
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
        return allInterfacesForClassAsSet.contains(AutoConfigurationSelector.class);
    }
}
