package com.gitee.starblues.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 禁用web环境. 如果该注解加入到入口类上, 表示
 * @author starBlues
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisablePluginWeb {
}
