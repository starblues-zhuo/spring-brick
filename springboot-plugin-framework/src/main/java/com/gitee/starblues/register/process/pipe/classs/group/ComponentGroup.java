package com.gitee.starblues.register.process.pipe.classs.group;

import com.gitee.starblues.register.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.register.process.pipe.classs.PluginClassProcess;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 分组存在注解: Component、Service
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ComponentGroup implements PluginClassGroup {


    /**
     * spring 组件bean.
     * 包括Component、Service
     */
    public static final String SPRING_COMPONENT= "spring_component";

    @Override
    public String groupId() {
        return SPRING_COMPONENT;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, Component.class, Service.class);
    }
}
