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

package com.gitee.starblues.core.checker;

import com.gitee.starblues.common.Constants;
import com.gitee.starblues.common.DependencyPlugin;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginManager;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.List;
import java.util.Objects;

/**
 * 插件依赖的插件检查者
 * @author starBlues
 * @version 3.0.0
 */
public class DependencyPluginLauncherChecker implements PluginLauncherChecker {

    private final PluginManager pluginManager;

    public DependencyPluginLauncherChecker(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * 检查插件依赖。如果依赖没有启动, 则自动启动
     * @param pluginInfo 插件信息
     * @throws PluginException PluginException
     */
    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
        List<DependencyPlugin> dependencyPlugins = pluginInfo.getPluginDescriptor().getDependencyPlugin();
        if(ObjectUtils.isEmpty(dependencyPlugins)){
            return;
        }
        PluginDescriptor descriptor = pluginInfo.getPluginDescriptor();
        resolveDependencyPlugin(pluginInfo, (dependencyPlugin, dependencyPluginInfo)->{
            String id = dependencyPlugin.getId();
            String version = dependencyPlugin.getVersion();
            boolean allowAllVersion = Constants.ALLOW_VERSION.equals(version);

            boolean findDependency = false;
            if(dependencyPluginInfo != null){
                if(allowAllVersion){
                    findDependency = true;
                } else {
                    findDependency = Objects.equals(dependencyPluginInfo.getPluginDescriptor().getPluginVersion(),
                            descriptor.getPluginVersion());
                }
            }
            String dependencyPluginUnique = MsgUtils.getPluginUnique(id, allowAllVersion ? null : version);
            if(!findDependency){
                throw new PluginException(descriptor, "需要依赖插件[" + dependencyPluginUnique  + "]才能启动");
            }
            if(dependencyPluginInfo.getPluginState() != PluginState.STARTED){
                // 没有启动的话, 手动启动
                try {
                    pluginManager.start(dependencyPluginInfo.getPluginId());
                } catch (Exception e){
                    if(e instanceof PluginDisabledException){
                        // 依赖被禁用, 不能启动
                        throw new PluginDisabledException(descriptor,
                                "依赖的插件[" + dependencyPluginUnique  + "]被禁用, 无法启动当前插件");
                    }
                    throw new PluginException(descriptor,
                            "依赖的插件[" + dependencyPluginUnique  + "]启动失败. 无法启动当前插件", e);
                }
            }
        });
    }

    @Override
    public void checkCanStop(PluginInfo pluginInfo) throws PluginException {
        // 忽略
    }

    private void resolveDependencyPlugin(PluginInfo pluginInfo, ResolveDependencyPlugin resolveDependencyPlugin)
            throws PluginException {
        List<DependencyPlugin> dependencyPlugins = pluginInfo.getPluginDescriptor().getDependencyPlugin();
        if(ObjectUtils.isEmpty(dependencyPlugins)){
            return;
        }
        for (DependencyPlugin dependencyPlugin : dependencyPlugins) {
            if (dependencyPlugin.getOptional()) {
                continue;
            }
            PluginInfo plugin = pluginManager.getPluginInfo(dependencyPlugin.getId());
            resolveDependencyPlugin.process(dependencyPlugin, plugin);
        }
    }

    @FunctionalInterface
    protected interface ResolveDependencyPlugin{
        /**
         * 处理依赖的插件
         * @param dependencyPlugin 当前依赖的插件
         * @param dependencyPluginInfo 查询出当前系统安装的依赖的插件信息。可能为空
         * @throws PluginException 处理异常
         */
        void process(DependencyPlugin dependencyPlugin, PluginInfo dependencyPluginInfo) throws PluginException;
    }

}
