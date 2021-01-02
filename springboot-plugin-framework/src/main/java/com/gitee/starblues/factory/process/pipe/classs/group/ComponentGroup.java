package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组存在注解: Component、Service
 *
 * @author starBlues
 * @version 2.1.0
 */
public class ComponentGroup implements PluginClassGroup {

    /**
     * spring 组件bean.
     * 包括Component、Service
     */
    public static final String GROUP_ID = "spring_component";


    private final List<PluginClassGroup> filters = new ArrayList<>();

    public ComponentGroup(){
        filters.add(new ConfigDefinitionGroup());
        filters.add(new ConfigBeanGroup());
        filters.add(new OneselfListenerGroup());
        filters.add(new CallerGroup());
        filters.add(new SupplierGroup());
    }


    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        boolean have = AnnotationsUtils.haveAnnotations(aClass, false, Component.class, Service.class);
        if(!have){
            return false;
        }
        // 进行基本组件Bean的过滤
        for (PluginClassGroup filter : filters) {
            if(filter.filter(aClass)){
                return false;
            }
        }
        return true;
    }
}
