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
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.ObjectUtils;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginLauncherChecker implements PluginLauncherChecker {


    protected final RealizeProvider realizeProvider;
    protected final IntegrationConfiguration configuration;


    public DefaultPluginLauncherChecker(RealizeProvider realizeProvider,
                                        IntegrationConfiguration configuration) {
        this.realizeProvider = realizeProvider;
        this.configuration = configuration;
    }


    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
        PluginDisabledException.checkDisabled(pluginInfo, configuration, "启动");
        PluginState pluginState = pluginInfo.getPluginState();
        if(pluginState == PluginState.STARTED){
            throw new PluginException(pluginInfo.getPluginDescriptor(), "已经启动, 不能再启动");
        }
        checkRequiresVersion(pluginInfo);
    }


    @Override
    public void checkCanStop(PluginInfo pluginInfo) throws PluginException {
        PluginDisabledException.checkDisabled(pluginInfo, configuration, "停止");
        PluginState pluginState = pluginInfo.getPluginState();
        if(pluginState != PluginState.STARTED){
            throw new PluginException(pluginInfo.getPluginDescriptor(), "没有启动, 不能停止");
        }
    }

    /**
     * 检查可安装到主程序的版本
     * @param pluginInfo pluginInfo
     */
    private void checkRequiresVersion(PluginInfo pluginInfo){
        String version = configuration.version();
        if(ObjectUtils.isEmpty(version) || Constants.ALLOW_VERSION.equals(version)){
            return;
        }
        String requires = pluginInfo.getPluginDescriptor().getRequires();
        boolean exactVersion = configuration.exactVersion();
        int compareVersion = realizeProvider.getVersionInspector().compareTo(requires, version);
        if(exactVersion && compareVersion != 0){
            String error = "需要安装到[" + requires + "]版本的主程序, 但当前主程序版本为[" + version + "]";
            throw new PluginException(pluginInfo.getPluginDescriptor(), error);
        }
        if(compareVersion > 0){
            String error = "需要安装到小于等于[" + requires + "]版本的主程序, 但当前主程序版本为[" + version + "]";
            throw new PluginException(pluginInfo.getPluginDescriptor(), error);
        }
    }





}
