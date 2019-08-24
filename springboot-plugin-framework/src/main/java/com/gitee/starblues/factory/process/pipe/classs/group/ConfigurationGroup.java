package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.context.annotation.Configuration;

/**
 * 分组存在注解: @Configuration
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ConfigurationGroup implements PluginClassGroup {

    /**
     * spring @CONFIGURATION 注解bean
     */
    public static final String SPRING_CONFIGURATION = "spring_configuration";


    @Override
    public String groupId() {
        return SPRING_CONFIGURATION;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, Configuration.class);
    }
}
