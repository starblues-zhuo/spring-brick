package com.gitee.starblues.utils;



import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 反射工具类
 * @author starBlues
 * @version 3.0.0
 */
public abstract class ReflectionUtils {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    private ReflectionUtils(){}

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    public static Field findField(Class<?> clazz, Class<?> fieldType) {
        return findField(clazz, null, fieldType);
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
        while (true){
            for (Field field : declaredFields) {
                if ((fieldName == null || fieldName.equals(field.getName())) &&
                        (fieldType == null || fieldType.equals(field.getType()))) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
            if(clazz == null){
                break;
            }
            declaredFields = clazz.getDeclaredFields();
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

    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Assert.isNotNull(clazz, "Class must not be null");
        Assert.isNotEmpty(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() :
                    getDeclaredMethods(searchType, false));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static void setAttribute(Object bean, String setMethodName, Object setObject) throws Exception {
        Class<?> aClass = bean.getClass();
        Method setMethod = ReflectionUtils.findMethod(aClass, setMethodName, setObject.getClass());

        if(setMethod == null){
            throw new Exception("Not found method[" + setMethodName + "] of :" + aClass.getName());
        }
        setMethod.invoke(bean, setObject);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
        Assert.isNotNull(clazz, "Class must not be null");
        Method[] result = new Method[]{};
        try {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);

            if (defaultMethods != null) {
                result = new Method[declaredMethods.length + defaultMethods.size()];
                System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                int index = declaredMethods.length;
                for (Method defaultMethod : defaultMethods) {
                    result[index] = defaultMethod;
                    index++;
                }
            } else {
                result = declaredMethods;
            }
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                    "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
        }
        return result;
    }

    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        if(paramTypes.length != method.getParameterCount()){
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Class<?> methodParamType = parameterTypes[i];
            if(!methodParamType.isAssignableFrom(paramType)){
                return false;
            }
        }
        return true;
    }

}
