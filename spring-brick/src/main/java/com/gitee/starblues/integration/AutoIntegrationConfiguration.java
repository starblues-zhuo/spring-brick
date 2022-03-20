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

import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.utils.ResourceUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;

/**
 * 自动集成的配置
 * @author starBlues
 * @version 3.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Component
@ConfigurationProperties(prefix = "plugin")
@Data
public class AutoIntegrationConfiguration extends DefaultIntegrationConfiguration{

    public static final String ENABLE_KEY = "plugin.enable";
    public static final String ENABLE_STARTER_KEY = "plugin.enableStarter";

    /**
     * 是否启用插件功能
     */
    @Value("${enable:true}")
    private Boolean enable;

    /**
     * 运行模式
     *  开发环境: development、dev
     *  生产/部署 环境: deployment、prod
     */
    @Value("${runMode:dev}")
    private String runMode;

    /**
     * 主程序包名
     */
    @Value("${mainPackage:}")
    private String mainPackage;

    /**
     * 插件的路径
     */
    private List<String> pluginPath;

    /**
     * 上传的插件所存储的临时目录
     */
    @Value("${uploadTempPath:temp}")
    private String uploadTempPath;

    /**
     * 在卸载插件后, 备份插件的目录
     */
    @Value("${backupPath:backupPlugin}")
    private String backupPath;

    /**
     * 插件rest接口前缀. 默认: /plugins
     */
    @Value("${pluginRestPathPrefix:/plugins}")
    private String pluginRestPathPrefix;

    /**
     * 是否启用插件id作为rest接口前缀, 默认为启用.
     * 如果为启用, 则地址为 /pluginRestPathPrefix/pluginId
     * pluginRestPathPrefix: 为pluginRestPathPrefix的配置值
     * pluginId: 为插件id
     */
    @Value("${pluginRestPathPrefix:true}")
    private Boolean enablePluginIdRestPathPrefix;

    /**
     * 启用的插件id
     */
    private Set<String> enablePluginIds;

    /**
     * 禁用的插件id, 禁用后系统不会启动该插件
     * 如果禁用所有插件, 则Set集合中返回一个字符: *
     */
    private Set<String> disablePluginIds;

    /**
     * 设置初始化时插件启动的顺序
     */
    private List<String> sortInitPluginIds;

    /**
     * 当前主程序的版本号, 用于校验插件是否可安装.
     * 插件中可通过插件配置信息 requires 来指定可安装的主程序版本
     * 如果为: 0.0.0 的话, 表示不校验
     */
    @Value("${version:0.0.0}")
    private String version;

    /**
     * 设置为true表示插件设置的requires的版本号完全匹配version版本号才可允许插件安装, 即: requires=x.y.z
     * 设置为false表示插件设置的requires的版本号小于等于version值, 插件就可安装, 即requires<=x.y.z
     * 默认为false
     */
    @Value("${exactVersion:false}")
    private Boolean exactVersion;

    @Override
    public boolean enable() {
        if(enable == null){
            return true;
        }
        return enable;
    }

    @Override
    public RuntimeMode environment() {
        return RuntimeMode.byName(runMode);
    }

    @Override
    public String mainPackage() {
        return ResourceUtils.replacePackage(mainPackage);
    }

    @Override
    public List<String> pluginPath() {
        return pluginPath;
    }

    @Override
    public String uploadTempPath() {
        if(ObjectUtils.isEmpty(uploadTempPath)){
            return super.uploadTempPath();
        }
        return uploadTempPath;
    }

    @Override
    public String backupPath() {
        if(ObjectUtils.isEmpty(backupPath)){
            return super.backupPath();
        }
        return backupPath;
    }

    @Override
    public String pluginRestPathPrefix() {
        if(pluginRestPathPrefix == null){
            return super.pluginRestPathPrefix();
        } else {
            return pluginRestPathPrefix;
        }
    }

    @Override
    public boolean enablePluginIdRestPathPrefix() {
        if(enablePluginIdRestPathPrefix == null){
            return super.enablePluginIdRestPathPrefix();
        } else {
            return enablePluginIdRestPathPrefix;
        }
    }

}
