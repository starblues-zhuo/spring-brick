package com.plugin.development.annotation;

import java.lang.annotation.*;

/**
 * 插件配置文件对应的bean定义注解
 * @author zhangzhuo
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigDefinition {


    /**
     * 插件中的配置文件的名称
     * @return 配置文件名称
     */
    String value();

}
