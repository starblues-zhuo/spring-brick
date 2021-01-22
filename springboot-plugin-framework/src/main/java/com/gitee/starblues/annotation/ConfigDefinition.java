package com.gitee.starblues.annotation;

import java.lang.annotation.*;

/**
 * 插件配置文件对应的bean定义注解
 * @author starBlues
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigDefinition {


    /**
     * 插件中的配置文件的名称
     * @return String
     */
    @Deprecated
    String value() default "";

    /**
     * 插件中的配置文件的名称
     * @return String
     */
    String fileName() default "";

    /**
     * 自定义 bean 名称
     * @return String
     */
    String beanName() default "";

    /**
     * 开发环境下文件后缀
     * 如果文件名称为: xxx.yml, 根据当前配置(当前配置为-dev)在开发环境下文件后缀为: xxx-dev.yml
     * @return 开发环境下文件名称后缀, 比如 dev
     */
    String devSuffix() default "";

    /**
     * 生产环境下文件后缀
     * 如果文件名称为: xxx.yml, 根据当前配置(当前配置为-prod)在生产环境下文件后缀为: xxx-prod.yml
     * @return 生产环境下文件名称后缀, 比如 -prod
     */
    String prodSuffix() default "";

}
