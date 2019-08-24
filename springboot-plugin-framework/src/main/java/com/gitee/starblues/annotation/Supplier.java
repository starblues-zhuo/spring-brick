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

    /**
     * 被调用者的方法注解。配合 @Caller.Method 使用.如果不定义, 则以方法名称为准。
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Method {
        /**
         * 方法名
         * @return String
         */
        String value();
    }

}
