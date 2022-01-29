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

package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.upload.UploadParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * 插件操作包装者
 * @author starBlues
 * @version 3.0.0
 */
public class PluginOperatorWrapper implements PluginOperator{

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PluginOperator pluginOperator;
    private final IntegrationConfiguration configuration;

    public PluginOperatorWrapper(PluginOperator pluginOperator,
                                 IntegrationConfiguration configuration) {
        this.pluginOperator = pluginOperator;
        this.configuration = configuration;
    }

    @Override
    public boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException {
        if(isDisable()){
            return false;
        }
        return pluginOperator.initPlugins(pluginInitializerListener);
    }

    @Override
    public boolean verify(Path jarPath) throws PluginException {
        if(isDisable()){
            return false;
        }
        return pluginOperator.verify(jarPath);
    }

    @Override
    public PluginInfo parse(Path pluginPath) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.parse(pluginPath);
    }

    @Override
    public PluginInfo install(Path jarPath, boolean unpackPlugin) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.install(jarPath, unpackPlugin);
    }

    @Override
    public boolean unload(String pluginId) throws PluginException {
        if(isDisable()){
            return false;
        }
        return pluginOperator.unload(pluginId);
    }

    @Override
    public void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException {
        if(isDisable()){
            return;
        }
        pluginOperator.uninstall(pluginId, isDelete, isBackup);
    }

    @Override
    public PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.install(pluginPath, unpackPlugin);
    }

    @Override
    public boolean start(String pluginId) throws PluginException {
        if(isDisable()){
            return false;
        }
        return pluginOperator.start(pluginId);
    }

    @Override
    public boolean stop(String pluginId) throws PluginException {
        if(isDisable()){
            return false;
        }
        return pluginOperator.stop(pluginId);
    }

    @Override
    public PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.uploadPlugin(uploadParam);
    }

    @Override
    public Path backupPlugin(Path backDirPath, String sign) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.backupPlugin(backDirPath, sign);
    }

    @Override
    public Path backupPlugin(String pluginId, String sign) throws PluginException {
        if(isDisable()){
            return null;
        }
        return pluginOperator.backupPlugin(pluginId, sign);
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        if(isDisable()){
            return Collections.emptyList();
        }
        return pluginOperator.getPluginInfo();
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        if(isDisable()){
            return null;
        }
        return pluginOperator.getPluginInfo(pluginId);
    }

    /**
     * 是否被禁用
     * @return true 禁用
     */
    private boolean isDisable(){
        if(configuration.enable()){
            return false;
        }
        // 如果禁用的话, 直接返回
        log.info("插件功能已被禁用!");
        return true;
    }

}
