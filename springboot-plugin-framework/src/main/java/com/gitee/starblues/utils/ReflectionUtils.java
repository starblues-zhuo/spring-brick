package com.gitee.starblues.utils;


import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 反射工具类
 * @author starBlues
 * @version 3.0.0
 */
public abstract class ReflectionUtils {

    private ReflectionUtils(){}

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    public static Field findField(Class<?> clazz, String fieldName,  Class<?> fieldType) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        if(fieldName == null && fieldType == null){
            throw new IllegalArgumentException("fieldName or fieldType of the field must be specified");
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        if(declaredFields.length == 0){
            return null;
        }
        for (Field field : declaredFields) {
            if ((fieldName == null || fieldName.equals(field.getName())) &&
                    (fieldType == null || fieldType.equals(field.getType()))) {
                return field;
            }
        }
        return null;
    }


    public static Object getField(Object o, String fieldName) {
        return getField(o, fieldName, null);
    }

    public static Object getField(Object o, String fieldName, Class<?> fieldType) {
        Class<?> currentClass = o.getClass();
        Field field = findField(currentClass, fieldName, fieldType);
        while (true){
            if(field != null) {
                break;
            }
            currentClass = currentClass.getSuperclass();
            if(currentClass == null){
                break;
            }
            field = findField(currentClass, fieldName, fieldType);
        }
        if(field == null){
            return null;
        }
        field.setAccessible(true);
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void setField(Object o, String fieldName, Object value) {
        setField(o, fieldName, null, value);
    }

    public static void setField(Object o, String fieldName, Class<?> fieldType, Object value) {
        Field field = findField(o.getClass(), fieldName, fieldType);
        if(field != null){
            field.setAccessible(true);
            try {
                field.set(o, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
