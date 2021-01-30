package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 自动集成的配置
 * @author starBlues
 * @version 2.4.0
 */
@Component
@ConfigurationProperties(prefix = "plugin")
public class AutoIntegrationConfiguration extends DefaultIntegrationConfiguration{

    /**
     * 运行模式
     *  开发环境: development、dev
     *  生产/部署 环境: deployment、prod
     */
    @Value("${runMode:dev}")
    private String runMode;

    /**
     * 是否启用插件功能
     */
    @Value("${enable:true}")
    private Boolean enable;

    /**
     * 插件的路径
     */
    @Value("${pluginPath:plugins}")
    private String pluginPath;

    /**
     * 插件文件的路径
     */
    @Value("${pluginConfigFilePath:pluginConfigs}")
    private String pluginConfigFilePath;

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
     * 是否启用Swagger刷新机制. 默认启用
     */
    @Value("${enableSwaggerRefresh:true}")
    private Boolean enableSwaggerRefresh;

    /**
     * 在卸载插件后, 备份插件的目录
     */
    @Value("${backupPath:}")
    private String backupPath;

    /**
     * 上传的插件所存储的临时目录
     */
    @Value("${uploadTempPath:}")
    private String uploadTempPath;

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

    @Override
    public RuntimeMode environment() {
        return RuntimeMode.byName(runMode);
    }

    @Override
    public String pluginPath() {
        return pluginPath;
    }

    @Override
    public String pluginConfigFilePath() {
        return pluginConfigFilePath;
    }


    @Override
    public boolean enable() {
        if(enable == null){
            return true;
        }
        return enable;
    }


    @Override
    public String uploadTempPath() {
        if(StringUtils.isNullOrEmpty(uploadTempPath)){
            return super.uploadTempPath();
        }
        return uploadTempPath;
    }

    @Override
    public String backupPath() {
        if(StringUtils.isNullOrEmpty(backupPath)){
            return super.backupPath();
        }
        return backupPath;
    }

    @Override
    public String pluginRestPathPrefix() {
        if(StringUtils.isNullOrEmpty(pluginRestPathPrefix)){
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


    @Override
    public Set<String> enablePluginIds() {
        return enablePluginIds;
    }

    @Override
    public Set<String> disablePluginIds() {
        return disablePluginIds;
    }

    @Override
    public boolean enableSwaggerRefresh() {
        return enableSwaggerRefresh;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getPluginConfigFilePath() {
        return pluginConfigFilePath;
    }

    public void setPluginConfigFilePath(String pluginConfigFilePath) {
        this.pluginConfigFilePath = pluginConfigFilePath;
    }

    public String getPluginRestPathPrefix() {
        return pluginRestPathPrefix;
    }

    public void setPluginRestPathPrefix(String pluginRestPathPrefix) {
        this.pluginRestPathPrefix = pluginRestPathPrefix;
    }

    public Boolean getEnablePluginIdRestPathPrefix() {
        return enablePluginIdRestPathPrefix;
    }

    public void setEnablePluginIdRestPathPrefix(Boolean enablePluginIdRestPathPrefix) {
        this.enablePluginIdRestPathPrefix = enablePluginIdRestPathPrefix;
    }

    public Boolean getEnableSwaggerRefresh() {
        return enableSwaggerRefresh;
    }

    public void setEnableSwaggerRefresh(Boolean enableSwaggerRefresh) {
        this.enableSwaggerRefresh = enableSwaggerRefresh;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public String getUploadTempPath() {
        return uploadTempPath;
    }

    public void setUploadTempPath(String uploadTempPath) {
        this.uploadTempPath = uploadTempPath;
    }

    public Set<String> getEnablePluginIds() {
        return enablePluginIds;
    }

    public void setEnablePluginIds(Set<String> enablePluginIds) {
        this.enablePluginIds = enablePluginIds;
    }

    public Set<String> getDisablePluginIds() {
        return disablePluginIds;
    }

    public void setDisablePluginIds(Set<String> disablePluginIds) {
        this.disablePluginIds = disablePluginIds;
    }

    public List<String> getSortInitPluginIds() {
        return sortInitPluginIds;
    }

    public void setSortInitPluginIds(List<String> sortInitPluginIds) {
        this.sortInitPluginIds = sortInitPluginIds;
    }
}
