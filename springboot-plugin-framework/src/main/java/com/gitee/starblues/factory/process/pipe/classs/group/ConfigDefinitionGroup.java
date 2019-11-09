package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;

/**
 * 分组存在注解: @ConfigDefinition
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class ConfigDefinitionGroup implements PluginClassGroup {

    /**
     * 自定义插件配置文件bean @ConfigDefinition
     */
    public static final String GROUP_ID= "config_definition";


    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, ConfigDefinition.class);
    }

}
