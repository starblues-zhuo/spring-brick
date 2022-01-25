package com.gitee.starblues.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 禁用web环境. 如果该注解加入到入口类上, 表示当前插件禁用web的功能。
 * 包括：controller注册、拦截器注册、web静态资源访问、thymeleaf模板引擎
 * @author starBlues
 * @version 3.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisablePluginWeb {
}
