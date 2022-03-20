/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 反射工具类
 *
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
        if(fieldName == null && fieldType == null){
            throw new IllegalArgumentException("fieldName or fieldType of the field must be specified");
        }
        return findField(clazz, (field)->{
            if ((fieldName == null || fieldName.equals(field.getName())) &&
                    (fieldType == null || fieldType.equals(field.getType()))) {
                return true;
            }
            return false;
        });
    }

    public static Field findField(Class<?> clazz, FieldFilter filter) {
        Objects.requireNonNull(clazz, "clazz must not be null");


        Field[] declaredFields = clazz.getDeclaredFields();
        while (true){
            for (Field field : declaredFields) {
                if (filter.filter(field)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
            if(clazz == null || clazz == Object.class){
                break;
            }
            declaredFields = clazz.getDeclaredFields();
        }

        return null;
    }


    public static Object getField(Object object, Class<?> objectClass, String fieldName) {
        return getField(object, objectClass, fieldName, null);
    }

    public static Object getField(Object object, Class<?> objectClass, String fieldName, Class<?> fieldType) {
        Field field = findField(objectClass, fieldName, fieldType);
        if(field == null){
            return null;
        }
        field.setAccessible(true);
        try {
            return field.get(object);
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
        List<Method> methods = findMethods(clazz, name, paramTypes);
        if(ObjectUtils.isEmpty(methods)){
            return null;
        } else {
            return methods.get(0);
        }
    }

    public static List<Method> findMethods(Class<?> clazz, String name, Class<?>... paramTypes){
        Assert.isNotNull(clazz, "Class must not be null");
        Assert.isNotEmpty(name, "Method name must not be null");
        Class<?> searchType = clazz;
        List<Method> methodList = new ArrayList<>();
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() :
                    getDeclaredMethods(searchType, false));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    methodList.add(method);
                }
            }
            searchType = searchType.getSuperclass();
        }
        return methodList;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object object, String methodName, Object... params) throws RuntimeException {
        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }
        Class<?> aClass = object.getClass();
        Method method = ReflectionUtils.findMethod(aClass, methodName, paramTypes);
        if(method == null){
            throw new RuntimeException("Not found method : " + methodToString(aClass, methodName, paramTypes));
        }
        try {
            Object invoke = method.invoke(object, params);
            if(invoke != null){
                return (T) invoke;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot call method : " + methodToString(aClass, methodName, paramTypes), e);
        }
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
            if(!CompareClassTypeUtils.compare(methodParamType, paramType)){
                return false;
            }
        }
        return true;
    }


    public static NoSuchMethodException getNoSuchMethodException(Class<?> aClass, String name, Class<?>[] argTypes) {
        return new NoSuchMethodException("Not found method:" + methodToString(aClass, name, argTypes));
    }

    public static String methodToString(Class<?> aClass, String name, Class<?>[] argTypes) {
        StringJoiner sj = new StringJoiner(", ", aClass.getName() + "." + name + "(", ")");
        if (argTypes != null) {
            for (Class<?> c : argTypes) {
                sj.add((c == null) ? "null" : c.getName());
            }
        }
        return sj.toString();
    }

}
