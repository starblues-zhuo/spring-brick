package com.gitee.starblues.extension.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置的默认值注解
 * @author sousouki
 * @version 2.4.3
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigItem {


    /**
     * 默认值
     * @return String
     */
    String defaultValue() default "";

}
