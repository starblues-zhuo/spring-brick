package com.gitee.starblues.annotation;

import java.lang.annotation.*;

/**
 * 被调用类的提供者。配合 @Caller 注解使用, 两者结合实现插件中的方法调用。
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Supplier {

    /**
     * 全局唯一key.全局不能重复
     * @return String
     */
    String value();

}
