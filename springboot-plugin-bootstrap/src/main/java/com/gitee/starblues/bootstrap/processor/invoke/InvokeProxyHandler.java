/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap.processor.invoke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 反射调用处理模块
 * @author starBlues
 * @version 3.0.0
 */
public class InvokeProxyHandler implements InvocationHandler {

    private final Caller callerAnnotation;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final InvokeSupperCache invokeSupperCache;

    public InvokeProxyHandler(Caller callerAnnotation, InvokeSupperCache invokeSupperCache) {
        this.callerAnnotation = callerAnnotation;
        this.invokeSupperCache = invokeSupperCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] callerArgs) throws Throwable {
        String pluginId = callerAnnotation.pluginId();
        Object supplierObject = invokeSupperCache.getSupperBean(pluginId, callerAnnotation.value());
        if (supplierObject == null) {
            if (ObjectUtils.isEmpty(pluginId)) {
                throw new Exception("Not found '" + callerAnnotation.value() + "' supplier object");
            } else {
                throw new Exception("Not found '" + callerAnnotation.value() + "' supplier object in plugin '" +
                        pluginId + "'");
            }
        }
        Caller.Method callerMethod = method.getAnnotation(Caller.Method.class);
        if (callerArgs == null) {
            callerArgs = new Object[]{};
        }
        if (callerMethod == null) {
            return notAnnotationInvoke(method, supplierObject, callerArgs);
        } else {
            return annotationInvoke(method, callerMethod, supplierObject, callerArgs);
        }
    }



    /**
     * 有注解的调用
     * @param method 调用接口的方法
     * @param callerMethod 调用者方法注解
     * @param supplierObject 调用者对象
     * @param callerArgs 调用者参数
     * @return 返回值
     * @throws Throwable 异常
     */
    private Object annotationInvoke(Method method, Caller.Method callerMethod,
                                    Object supplierObject, Object[] callerArgs) throws Throwable{

        String callerMethodName = callerMethod.value();
        Class<?> supplierClass = supplierObject.getClass();
        Method[] methods = supplierClass.getMethods();
        Method supplierMethod = null;
        for (Method m : methods) {
            Supplier.Method supplierMethodAnnotation = m.getAnnotation(Supplier.Method.class);
            if(supplierMethodAnnotation == null){
                continue;
            }
            if(Objects.equals(supplierMethodAnnotation.value(), callerMethodName)){
                supplierMethod = m;
                break;
            }
        }
        if(supplierMethod == null){
            // 如果为空, 说明没有找到被调用者的注解, 则走没有注解的代理调用。
            return notAnnotationInvoke(method, supplierObject, callerArgs);
        }
        Class<?>[] parameterTypes = supplierMethod.getParameterTypes();
        if(parameterTypes.length != callerArgs.length){
            // 参数不匹配
            return notAnnotationInvoke(method, supplierObject, callerArgs);
        }
        Object[] supplierArgs = getSupplierArgs(callerArgs, supplierMethod);
        Object invokeReturn = supplierMethod.invoke(supplierObject, supplierArgs);
        return getReturnObject(invokeReturn, method);
    }

    /**
     * 没有注解调用
     * @param method 调用接口的方法
     * @param supplierObject 提供者对象
     * @param callerArgs 调用者参数
     * @return 返回值
     * @throws Throwable 异常
     */
    private Object notAnnotationInvoke(Method method, Object supplierObject, Object[] callerArgs) throws Throwable{
        String name = method.getName();
        Class<?>[] supplierArgClasses = new Class[callerArgs.length];
        ClassLoader classLoader = supplierObject.getClass().getClassLoader();
        for (int i = 0; i < callerArgs.length; i++) {
            Object callerArg = callerArgs[i];
            try {
                supplierArgClasses[i] = classLoader.loadClass(callerArg.getClass().getName());
            } catch (Exception e){
                supplierArgClasses[i] = callerArg.getClass();
            }
        }
        Class<?> supplierClass = supplierObject.getClass();
        Method supplierMethod = null;
        try {
            supplierMethod = supplierClass.getMethod(name, supplierArgClasses);
        } catch (Exception e){
            supplierMethod = findSupplierMethod(supplierClass, name, supplierArgClasses);
        }
        if(supplierMethod == null){
            throw ReflectionUtils.getNoSuchMethodException(supplierClass, name, supplierArgClasses);
        }
        Object[] supplierArgs = getSupplierArgs(callerArgs, supplierMethod);
        Object invokeReturn = supplierMethod.invoke(supplierObject, supplierArgs);
        return getReturnObject(invokeReturn, method);
    }

    private Object[] getSupplierArgs(Object[] callerArgs, Method supplierMethod) throws Exception{
        if(callerArgs == null || callerArgs.length == 0){
            return new Class<?>[]{};
        }
        Class<?>[] supplierParameterTypes = supplierMethod.getParameterTypes();
        Object[] supplierArgs = new Object[callerArgs.length];
        for (int i = 0; i < supplierParameterTypes.length; i++) {
            Class<?> supplierParameterType = supplierParameterTypes[i];
            Object arg = callerArgs[i];
            if(supplierParameterType.isAssignableFrom(arg.getClass())){
                // 类型相同
                supplierArgs[i] = arg;
            } else {
                // 类型不匹配, 尝试使用json序列化. 当前序列化针对大数据量下性能比较低, 建议将大数据量传输的参数定义到主程序中
                String json = OBJECT_MAPPER.writeValueAsString(arg);
                Object serializeObject = OBJECT_MAPPER.readValue(json, supplierParameterType);
                supplierArgs[i] = serializeObject;
            }
        }
        return supplierArgs;
    }


    /**
     * 得到返回值对象
     * @param invokeReturn 反射调用后返回的对象
     * @param method 调用接口的方法
     * @return 返回值对象
     * @throws Throwable Throwable
     */
    private Object getReturnObject(Object invokeReturn, Method method) throws Throwable{
        if(invokeReturn == null){
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if(invokeReturn.getClass().isAssignableFrom(returnType)){
            return invokeReturn;
        } else {
            String json = OBJECT_MAPPER.writeValueAsString(invokeReturn);
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructType(method.getGenericReturnType()) );
        }
    }

    private Method findSupplierMethod(Class<?> supplierClass, String methodName, Class<?>[] supplierArgClasses){
        while (supplierClass != null){
            Method[] methods = supplierClass.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                if(!Objects.equals(name, methodName)){
                    continue;
                }
                if(method.getParameterTypes().length == supplierArgClasses.length){
                    return method;
                }
            }
            supplierClass = supplierClass.getSuperclass();
        }
        return null;
    }

}