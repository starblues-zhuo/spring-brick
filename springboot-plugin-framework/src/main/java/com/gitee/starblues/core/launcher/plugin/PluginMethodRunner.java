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

package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 插件方法运行器。
 * @author starBlues
 * @version 3.0.0
 */
public class PluginMethodRunner extends MethodRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PLUGIN_RUN_METHOD_NAME = "run";

    private final PluginInteractive pluginInteractive;

    public PluginMethodRunner(PluginInteractive pluginInteractive) {
        super(pluginInteractive.getPluginDescriptor().getPluginBootstrapClass(), PLUGIN_RUN_METHOD_NAME, new String[]{});
        this.pluginInteractive = pluginInteractive;
        String args = pluginInteractive.getPluginDescriptor().getArgs();
        if(!ObjectUtils.isEmpty(args)){
            super.args = args.split(" ");
        }
    }

    @Override
    protected Class<?> loadRunClass(ClassLoader classLoader) throws Exception {
        try {
            return super.loadRunClass(classLoader);
        } catch (Exception e){
            if(e instanceof ClassNotFoundException){
                String pluginId = pluginInteractive.getPluginDescriptor().getPluginId();
                String error = "插件[" + pluginId + "]没有发现" + "[" + className + "]引导类";
                if(pluginInteractive.getConfiguration().environment() == RuntimeMode.DEV){
                    error = error + ", 请确保已经编译！";
                }
                throw new ClassNotFoundException(error);
            }
            throw e;
        }
    }

    @Override
    protected Object runMethod(Class<?> runClass) throws Exception {
        Method runMethod = ReflectionUtils.findMethod(runClass, runMethodName, Class.class, String[].class);
        if(runMethod == null) {
            throw new NoSuchMethodException(runClass.getName() + "." + runMethodName
                    + "(Class<?> arg0, String[] arg1)");
        }
        Object instance = getInstance(runClass);
        setPluginInteractive(instance);
        runMethod.setAccessible(true);
        try {
            return runMethod.invoke(instance, runClass, this.args);
        } catch (Exception e){
            String error = "Invoke failure: "
                    + ReflectionUtils.methodToString(runClass, runMethodName, runMethod.getParameterTypes())
                    + ". " + e.getMessage();
            logger.error(error, e);
            throw new Exception(error);
        }
    }

    private void setPluginInteractive(Object launchObject) throws Exception {
        if(launchObject == null){
            return;
        }
        ReflectionUtils.setAttribute(launchObject, "setPluginInteractive", pluginInteractive);
    }


}
