package com.gitee.starblues.bootstrap.utils;

import com.gitee.starblues.utils.ReflectionUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 销毁工具类
 * @author starBlues
 * @version 3.0.0
 */
public class DestroyUtils {

    private DestroyUtils(){}

    public static void destroyAll(Object object, String fieldName, Class<?> fieldType){
        destroyAll(object, object.getClass(), fieldName, fieldType);
    }

    public static void destroyAll(Object object, Class<?> objectClass, String fieldName, Class<?> fieldType){
        try {
            Object fieldObject = ReflectionUtils.getField(object, objectClass, fieldName, fieldType);
            if(fieldObject == null){
                return;
            }
            destroyAll(fieldObject);
        } catch (Exception e){
            // 忽略
        }
    }

    public static void destroyValue(Object object, String fieldName, Object value){
        destroyValue(object, object.getClass(), fieldName, value);
    }

    public static void destroyValue(Object object, Class<?> objectClass, String fieldName, Object value){
        destroyValue(object, objectClass, fieldName, null, value);
    }


    public static void destroyValue(Object object, Class<?> objectClass, String fieldName, Class<?> fieldType, Object value){
        try {
            Object fieldObject = ReflectionUtils.getField(object, objectClass, fieldName, fieldType);
            if(fieldObject == null){
                return;
            }
            destroyValue(fieldObject, value);
        } catch (Exception e){
            // 忽略
        }
    }

    @SuppressWarnings("all")
    public static void destroyAll(Object object){
        if(object == null){
            return;
        }
        if(object instanceof Map){
            ((Map)object).clear();
        } else if (object instanceof Collection){
            ((Collection)object).clear();
        } else if(object.getClass().isArray()){
            Object[] array = (Object[])object;
            for (int i = 0; i < array.length; i++) {
                array[i] = null;
            }
        }
    }

    @SuppressWarnings("all")
    public static void destroyValue(Object object, Object value){
        if(object == null){
            return;
        }
        if(object instanceof Map){
            ((Map)object).remove(value);
        } else if (object instanceof Collection){
            ((Collection) object).removeIf(next -> Objects.equals(next, value));
        }
    }

}
