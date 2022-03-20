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

package com.gitee.starblues.loader.launcher;

/**
 * 抽象的启动引导者
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractLauncher<R> implements Launcher<R> {

    @Override
    public R run(String... args) throws Exception {
        ClassLoader classLoader = createClassLoader(args);
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(classLoader);
            return launch(classLoader, args);
        } finally {
            thread.setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * 创建classloader
     * @return ClassLoader
     * @throws Exception 创建异常
     */
    protected abstract ClassLoader createClassLoader(String... args) throws Exception;

    /**
     * 子类实现具体的启动方法
     * @param classLoader 当前的 ClassLoader
     * @param args 启动参数
     * @return 启动返回值
     * @throws Exception 启动异常
     */
    protected abstract R launch(ClassLoader classLoader, String... args) throws Exception;

}
