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

package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk 同类型参数的代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public class JdkSameTypeParamProxyFactory implements ProxyFactory, InvocationHandler {

    protected final Object target;

    public JdkSameTypeParamProxyFactory(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(Class<T> interfacesClass) {
        ClassLoader classLoader = interfacesClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{ interfacesClass };
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ReflectionUtils.invoke(target, method.getName(), args);
    }

}
