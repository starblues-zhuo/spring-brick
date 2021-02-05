package com.gitee.starblues.annotation;

import java.lang.annotation.*;

/**
 * 基于业务的扩展注解
 * @author starBlues
 * @version 2.4.1
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
    String useCase() default "";

    /**
     * 用例
     * @return 用例标志
     */
    String scene() default "";

}
