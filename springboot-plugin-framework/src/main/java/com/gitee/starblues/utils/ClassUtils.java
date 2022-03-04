/**
 * Copyright [2019-2022] [starBlues]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 类工具类
 *
 * @author starBlues
 * @version 2.4.0
 */
public class ClassUtils {

    private ClassUtils() {

    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 通过反射获取字段
     *
     * @param o 对象
     * @param fieldName 字段名称
     * @param <T> 字段类型
     * @return 字段值
     * @throws IllegalAccessException 异常信息
     */
    public static <T> T getReflectionField(Object o, String fieldName) throws IllegalAccessException {
        if (o == null) {
            return null;
        }

        Field templateResolversField = ReflectionUtils.findField(o.getClass(),
                fieldName);
        return getReflectionField(templateResolversField, o);
    }

    /**
     * 通过反射获取字段
     *
     * @param o 对象
     * @param fieldName 字段名称
     * @param fieldClassType 字段类型
     * @param <T> 字段类型
     * @return 字段值
     * @throws IllegalAccessException 异常信息
     */
    public static <T> T getReflectionField(Object o, String fieldName, Class<?> fieldClassType) throws IllegalAccessException {
        if (o == null) {
            return null;
        }
        Field templateResolversField = ReflectionUtils.findField(o.getClass(),
                fieldName, fieldClassType);
        return getReflectionField(templateResolversField, o);
    }

    /**
     * 通过反射Field获取字段
     *
     * @param field Field字段
     * @param o 当前对象
     * @param <T> 字段类型
     * @return 字段值
     * @throws IllegalAccessException 异常信息
     */
    public static <T> T getReflectionField(Field field, Object o) throws IllegalAccessException {
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        Object fieldObject = field.get(o);
        return (T) fieldObject;
    }

    /**
     * 得到注解修改者
     *
     * TODO 可能某个java版本不生效
     * @param annotation 注解
     * @return 修改者集合
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAnnotationsUpdater(Object annotation) throws Exception {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field field = getAnnotationsUpdaterField(invocationHandler);
        if (field == null) {
            return null;
        }
        return (Map<String, Object>) field.get(invocationHandler);
    }

    private static Field getAnnotationsUpdaterField(InvocationHandler invocationHandler) {
        Class<? extends InvocationHandler> aClass = invocationHandler.getClass();
        Field field = ReflectionUtils.findField(aClass, "memberValues", Map.class);
        if (field == null) {
            field = ReflectionUtils.findField(aClass, "valueCache", Map.class);
        }
        if (field == null) {
            field = ReflectionUtils.findField(aClass, Map.class);
        }
        if (field != null) {
            field.setAccessible(true);
            return field;
        } else {
            return null;
        }
    }

}
