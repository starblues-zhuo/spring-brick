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

package com.gitee.starblues.core.launcher;

import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * 反射运行方法
 * @author starBlues
 * @version 3.0.0
 */
public class MethodRunner {

    protected final String className;
    protected final String runMethodName;

    protected final String[] args;

    public MethodRunner(String className, String runMethodName, String[] args) {
        this.className =  Assert.isNotEmpty(className, "className 不能为空");
        this.runMethodName = Assert.isNotEmpty(runMethodName, "runMethod 不能为空");
        this.args = (args != null) ? args.clone() : null;
    }

    public Object run() throws Exception {
        return run(null);
    }

    public Object run(ClassLoader classLoader) throws Exception {
        Class<?> runClass = loadRunClass(classLoader);
        return runMethod(runClass);
    }

    protected Class<?> loadRunClass(ClassLoader classLoader) throws Exception{
        if(classLoader == null){
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return Class.forName(this.className, false, classLoader);
    }

    protected Object runMethod(Class<?> runClass) throws Exception {
        Method runMethod = ReflectionUtils.findMethod(runClass, runMethodName, String[].class);
        if(runMethod == null) {
            throw new NoSuchMethodException(runClass.getName() + "." + runMethodName + "(String[] args)");
        }
        Object instance = getInstance(runClass);
        runMethod.setAccessible(true);
        runMethod.invoke(instance, new Object[] { this.args });
        return instance;
    }

    protected Object getInstance(Class<?> runClass) throws Exception {
        return runClass.getConstructor().newInstance();
    }

}
