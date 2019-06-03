package com.plugin.development.annotation;

import java.lang.annotation.*;

/**
 * @Description: 插件配置文件对应的bean定义注解
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 11:11
 * @Update Date Time:
 * @see
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigDefinition {


    /**
     * 插件中的配置文件的名称
     * @return
     */
    String value();

}
