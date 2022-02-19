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
import com.gitee.starblues.loader.PluginResourceStorage;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.web.PluginStaticResourceResolver;

/**
 * 默认的插件启动介入者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginLaunchInvolved implements PluginLaunchInvolved{

    @Override
    public void before(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        PluginResourceStorage.addPlugin(descriptor.getPluginId(), descriptor.getPluginFileName());
    }

    @Override
    public void after(InsidePluginDescriptor descriptor, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        PluginStaticResourceResolver.parse(descriptor, classLoader, pluginHook.getWebConfig());
    }

    @Override
    public void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        String pluginId = descriptor.getPluginId();
        PluginResourceStorage.removePlugin(pluginId);
        PluginStaticResourceResolver.remove(pluginId);
    }
}
