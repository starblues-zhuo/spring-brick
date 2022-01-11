package com.gitee.starblues.bootstrap.utils;

import com.gitee.starblues.utils.ReflectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
public class DestroyUtils {

    private DestroyUtils(){}


    public static void destroy(Object o, String fieldName, Class<?> fieldType){
        try {
            Object fieldObject = ReflectionUtils.getField(o, fieldName, fieldType);
            if(fieldObject == null){
                return;
            }
            destroy(fieldObject);
        } catch (Exception e){
            // 忽略
        }
    }

    @SuppressWarnings("all")
    public static void destroy(Object object){
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

}
