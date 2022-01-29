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

package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class PluginApplicationContextGetter implements PluginLaunchInvolved{

    private static final Map<String, ApplicationContext> PLUGIN_CONTEXTS = new ConcurrentHashMap<>();

    @Override
    public void after(InsidePluginDescriptor descriptor, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        PLUGIN_CONTEXTS.put(descriptor.getPluginId(), pluginHook.getApplicationContext());
    }

    @Override
    public void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        PLUGIN_CONTEXTS.remove(descriptor.getPluginId());
    }

    public static ApplicationContext get(String pluginId){
        return PLUGIN_CONTEXTS.get(pluginId);
    }

    public static Map<String, ApplicationContext> get(){
        return Collections.unmodifiableMap(PLUGIN_CONTEXTS);
    }

}
