package com.gitee.starblues.annotation;

import com.gitee.starblues.utils.OrderPriority;

import java.lang.annotation.*;

/**
 * 基于业务的扩展注解
 * @author starBlues
 * @version 2.4.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Extract {

    /**
     * 业务
     * @return 业务标志
     */
    String bus();

    /**
     * 场景
     * @return 场景标志
     */
    String scene() default "";

    /**
     * 用例
     * @return 用例标志
     */
    String useCase() default "";

    /**
     * 不同插件存在不同业务时, 用于指定优先级别. 数字越大, 优先级别越高
     * @return 优先级别
     */
    int order() default 0;

}
