package com.gitee.starblues.utils;

import java.util.jar.Attributes;

/**
 * @author starBlues
 * @version 1.0
 */
public abstract class ManifestUtils {

    private ManifestUtils(){}

    public static String getValue(Attributes attributes, String key){
        return getValue(attributes, key, true);
    }

    public static String getValue(Attributes attributes, String key, boolean notExitsThrowException){
        boolean throwException = false;
        String value = null;
        try {
            value = attributes.getValue(key);
            if(value == null && notExitsThrowException){
                throwException = true;
            }
        } catch (Throwable e){
            // 忽略
            throwException = true;
        }
        if(throwException){
            throw new IllegalStateException("Not found '" + key + "' from " + attributes.getClass().getName());
        }
        return value;
    }

}
