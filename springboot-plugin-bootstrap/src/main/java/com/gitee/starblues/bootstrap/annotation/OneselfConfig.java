package com.gitee.starblues.bootstrap.annotation;

import java.lang.annotation.*;

/**
 * 插件自主运行配置
 * @author starBlues
 * @version 3.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OneselfConfig {

    /**
     * 主程序配置文件名称
     * @return String[]
     */
    String[] mainConfigFileName() default {};


}
