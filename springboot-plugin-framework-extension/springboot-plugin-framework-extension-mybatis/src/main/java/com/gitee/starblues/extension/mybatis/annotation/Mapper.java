package com.gitee.starblues.extension.mybatis.annotation;

import java.lang.annotation.*;

/**
 * Mybatis Mapper 接口的注解
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Mapper {
}
