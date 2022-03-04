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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可缓存的代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public class CacheJdkSameTypeParamProxyFactory extends JdkSameTypeParamProxyFactory{

    private final Map<Method, Method> methodCache = new ConcurrentHashMap<>();

    public CacheJdkSameTypeParamProxyFactory(Object target) {
        super(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = methodCache.get(method);
        if(targetMethod == null){
            Class<?>[] paramTypes = null;
            if(args != null){
                paramTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramTypes[i] = args[i].getClass();
                }
            }
            targetMethod = ReflectionUtils.findMethod(target.getClass(), method.getName(), paramTypes);
            if(targetMethod != null){
                methodCache.put(method, targetMethod);
            } else {
                throw ReflectionUtils.getNoSuchMethodException(target.getClass(), method.getName(), paramTypes);
            }
        }
        return targetMethod.invoke(target, args);
    }

}
