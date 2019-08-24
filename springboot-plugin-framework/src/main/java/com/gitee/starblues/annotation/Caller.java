package com.gitee.starblues.annotation;

/**
 * 调用者的注解。配合 @Supplier 注解使用, 两者结合实现插件中的方法调用。
 *
 * @author zhangzhuo
 * @version 1.0
 */

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caller {

    /**
     * 调用者的全局唯一key. 也就是Supplier 中定义的key.
     * @return String
     */
    String value();

}
