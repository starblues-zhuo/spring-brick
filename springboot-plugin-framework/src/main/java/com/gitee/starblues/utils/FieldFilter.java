package com.gitee.starblues.utils;

import java.lang.reflect.Field;

/**
 * @author starBlues
 * @version 1.0
 */
@FunctionalInterface
public interface FieldFilter {

    /**
     * 过滤
     * @param field 当前字段
     * @return true 允许, false 不允许
     */
    boolean filter(Field field);


}
