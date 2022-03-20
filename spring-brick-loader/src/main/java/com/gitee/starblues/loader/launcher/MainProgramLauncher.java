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

import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.loader.utils.ObjectUtils;


import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 主程序启动者
 * @author starBlues
 * @version 3.0.0
 */
public class MainProgramLauncher extends AbstractLauncher<ClassLoader>{

    public static final String MAIN_CLASS_LOADER_NAME = "MainProgramLauncherClassLoader";

    private final MethodRunner methodRunner;

    public MainProgramLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        GenericClassLoader classLoader = new GenericClassLoader(MAIN_CLASS_LOADER_NAME, getParentClassLoader(),
                getResourceLoaderFactory());
        addResource(classLoader);
        return classLoader;
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        return classLoader;
    }

    protected ResourceLoaderFactory getResourceLoaderFactory(String... args){
        return ResourceLoaderFactoryGetter.get(MAIN_CLASS_LOADER_NAME, args);
    }

    protected ClassLoader getParentClassLoader(){
        return MainProgramLauncher.class.getClassLoader();
    }

    protected void addResource(GenericClassLoader classLoader) throws Exception{
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        if(!ObjectUtils.isEmpty(classPath)){
            String[] classPathStr = classPath.split(";");
            for (String path : classPathStr) {
                classLoader.addResource(path);
            }
        }
        ClassLoader sourceClassLoader = Thread.currentThread().getContextClassLoader();
        if(sourceClassLoader instanceof URLClassLoader){
            URLClassLoader urlClassLoader = (URLClassLoader) sourceClassLoader;
            final URL[] urLs = urlClassLoader.getURLs();
            for (URL url : urLs) {
                classLoader.addResource(url);
            }
        }
    }

}
