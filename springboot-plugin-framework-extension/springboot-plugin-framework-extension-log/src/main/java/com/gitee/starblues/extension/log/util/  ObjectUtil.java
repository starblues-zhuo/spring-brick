package com.gitee.starblues.extension.log.util;

import java.io.IOException;
import java.io.InputStream;

public class ObjectUtil {

    public static Object parseBasicTypeValue(Class<?> fieldType, String defaultValue) {
        Object fixedValue;
        if (Byte.class == fieldType || byte.class == fieldType) {
            fixedValue = Byte.parseByte(defaultValue);
        } else if (Integer.class == fieldType || int.class == fieldType) {
            fixedValue = Integer.parseInt(defaultValue);
        } else if (Double.class == fieldType || double.class == fieldType) {
            fixedValue = Double.parseDouble(defaultValue);
        } else if (Short.class == fieldType || short.class == fieldType) {
            fixedValue = Short.parseShort(defaultValue);
        } else if (Long.class == fieldType || long.class == fieldType) {
            fixedValue = Long.parseLong(defaultValue);
        } else if (Float.class == fieldType || float.class == fieldType) {
            fixedValue = Float.parseFloat(defaultValue);
        } else if (Boolean.class == fieldType || boolean.class == fieldType) {
            fixedValue = Boolean.parseBoolean(defaultValue);
        } else if (Character.class == fieldType || char.class == fieldType) {
            fixedValue = defaultValue.charAt(0);
        } else {
            fixedValue = defaultValue;
        }
        return fixedValue;
    }

    public static boolean isEmptyObject(Class<?> fieldType, Object fieldValue) {
        if (Byte.class == fieldType || byte.class == fieldType) {
            return (byte) fieldValue == 0;
        } else if (Integer.class == fieldType || int.class == fieldType) {
            return (int) fieldValue == 0;
        } else if (Double.class == fieldType || double.class == fieldType) {
            return (double) fieldValue == 0;
        } else if (Short.class == fieldType || short.class == fieldType) {
            return (short) fieldValue == 0;
        } else if (Long.class == fieldType || long.class == fieldType) {
            return (long) fieldValue == 0;
        } else if (Float.class == fieldType || float.class == fieldType) {
            return (float) fieldValue == 0;
        }
        return false;
    }

}
