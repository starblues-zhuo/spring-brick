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

package com.gitee.starblues.integration;

import com.gitee.starblues.utils.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 默认的插件集成配置。给非必须配置设置了默认值
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class DefaultIntegrationConfiguration implements IntegrationConfiguration{

    public static final String DEFAULT_PLUGIN_REST_PATH_PREFIX = "plugins";
    public static final boolean DEFAULT_ENABLE_PLUGIN_ID_REST_PATH_PREFIX = true;

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public List<String> pluginPath() {
        List<String> pluginPath = new ArrayList<>(1);
        pluginPath.add("~/plugins/");
        return pluginPath;
    }

    @Override
    public String uploadTempPath(){
        return "temp";
    }

    @Override
    public String backupPath(){
        return "backupPlugin";
    }

    @Override
    public String pluginRestPathPrefix(){
        return DEFAULT_PLUGIN_REST_PATH_PREFIX;
    }

    @Override
    public boolean enablePluginIdRestPathPrefix() {
        return DEFAULT_ENABLE_PLUGIN_ID_REST_PATH_PREFIX;
    }

    @Override
    public Set<String> enablePluginIds() {
        return null;
    }

    @Override
    public Set<String> disablePluginIds() {
        return null;
    }

    @Override
    public List<String> sortInitPluginIds() {
        return null;
    }

    @Override
    public String version() {
        return "0.0.0";
    }

    @Override
    public boolean exactVersion() {
        return false;
    }

    /**
     * 检查配置
     */
    @Override
    public void checkConfig(){
        Assert.isNotEmpty(mainPackage(), "插件配置: mainPackage 不能为空");
    }

}
