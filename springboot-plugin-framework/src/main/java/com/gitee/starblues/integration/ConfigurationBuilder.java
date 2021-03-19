package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 通过构造者进行配置插件初始化配置
 *
 * @author starBlues
 * @version 2.4.2
 */
public class ConfigurationBuilder extends DefaultIntegrationConfiguration{

    private boolean enable;

    private RuntimeMode runtimeMode = RuntimeMode.DEVELOPMENT;
    private String pluginPath = "";
    private String pluginConfigFilePath = "";

    private String uploadTempPath;
    private String backupPath;
    private String pluginRestPathPrefix;

    private Boolean enablePluginIdRestPathPrefix;

    private Set<String> enablePluginIds;
    private Set<String> disablePluginIds;
    private List<String> sortInitPluginIds;
    private Boolean enableSwaggerRefresh;

    private String version;
    private Boolean exactVersionAllowed;

    private Boolean enableWebSocket;

    public ConfigurationBuilder(Builder builder) {
        this.runtimeMode = Objects.requireNonNull(builder.runtimeMode, "runtimeMode can't be empty");
        this.pluginPath = Objects.requireNonNull(builder.pluginPath, "pluginPath can't be empty");
        this.pluginConfigFilePath = Objects.requireNonNull(builder.pluginConfigFilePath,
                "pluginConfigFilePath can't be empty");
        this.uploadTempPath = builder.uploadTempPath;
        this.backupPath = builder.backupPath;
        this.pluginRestPathPrefix = builder.pluginRestPathPrefix;
        this.enablePluginIdRestPathPrefix = builder.enablePluginIdRestPathPrefix;
        this.enablePluginIds = builder.enablePluginIds;
        this.disablePluginIds = builder.disablePluginIds;
        this.sortInitPluginIds = builder.sortInitPluginIds;
        this.version = builder.version;
        this.exactVersionAllowed = builder.exactVersionAllowed;
        if(builder.enable == null){
            this.enable = true;
        } else {
            this.enable = builder.enable;
        }
        if(builder.enableSwaggerRefresh == null){
            this.enableSwaggerRefresh = true;
        } else {
            this.enableSwaggerRefresh = builder.enableSwaggerRefresh;
        }
        if(builder.enableWebSocket == null){
            this.enableWebSocket = false;
        } else {
            this.enableWebSocket = builder.enableWebSocket;
        }
    }

    public static Builder toBuilder(){
        return new Builder();
    }

    public static class Builder{

        private Boolean enable;

        private RuntimeMode runtimeMode = RuntimeMode.DEVELOPMENT;
        private String pluginPath = "";
        private String pluginConfigFilePath = "";

        private String uploadTempPath;
        private String backupPath;
        private String pluginRestPathPrefix;
        private Boolean enablePluginIdRestPathPrefix;

        private Set<String> enablePluginIds;
        private Set<String> disablePluginIds;
        private List<String> sortInitPluginIds;
        private Boolean enableSwaggerRefresh;

        private String version;
        private Boolean exactVersionAllowed;

        private Boolean enableWebSocket;

        public Builder runtimeMode(RuntimeMode runtimeMode){
            this.runtimeMode = runtimeMode;
            return this;
        }

        public Builder enable(Boolean enable){
            this.enable = enable;
            return this;
        }

        public Builder pluginPath(String pluginPath){
            this.pluginPath = pluginPath;
            return this;
        }

        public Builder pluginConfigFilePath(String pluginConfigFilePath){
            this.pluginConfigFilePath = pluginConfigFilePath;
            return this;
        }

        public Builder uploadTempPath(String uploadTempPath){
            this.uploadTempPath = uploadTempPath;
            return this;
        }

        public Builder backupPath(String backupPath){
            this.backupPath = backupPath;
            return this;
        }

        public Builder pluginRestPathPrefix(String pluginRestPathPrefix){
            this.pluginRestPathPrefix = pluginRestPathPrefix;
            return this;
        }

        public Builder enablePluginIdRestPathPrefix(Boolean enablePluginIdRestPathPrefix){
            this.enablePluginIdRestPathPrefix = enablePluginIdRestPathPrefix;
            return this;
        }

        public Builder enablePluginIds(Set<String> enablePluginIds){
            this.enablePluginIds = enablePluginIds;
            return this;
        }

        public Builder disablePluginIds(Set<String> disablePluginIds){
            this.disablePluginIds = disablePluginIds;
            return this;
        }

        public Builder sortInitPluginIds(List<String> sortInitPluginIds){
            this.sortInitPluginIds = sortInitPluginIds;
            return this;
        }

        public Builder enableSwaggerRefresh(Boolean enableSwaggerRefresh){
            this.enableSwaggerRefresh = enableSwaggerRefresh;
            return this;
        }

        public Builder version(String version){
            this.version = version;
            return this;
        }

        public Builder exactVersionAllowed(Boolean exactVersionAllowed){
            this.exactVersionAllowed = exactVersionAllowed;
            return this;
        }

        public Builder enableWebSocket(Boolean enableWebSocket){
            this.enableWebSocket = enableWebSocket;
            return this;
        }

        public ConfigurationBuilder build(){
            return new ConfigurationBuilder(this);
        }

    }


    @Override
    public RuntimeMode environment() {
        return runtimeMode;
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
    public String uploadTempPath() {
        if(StringUtils.isNullOrEmpty(uploadTempPath)){
            return super.uploadTempPath();
        } else {
            return uploadTempPath;
        }
    }

    @Override
    public String backupPath() {
        if(StringUtils.isNullOrEmpty(backupPath)){
            return super.backupPath();
        } else {
            return backupPath;
        }
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
    public boolean enable() {
        return enable;
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
    public List<String> sortInitPluginIds() {
        return sortInitPluginIds;
    }

    @Override
    public boolean enableSwaggerRefresh() {
        if(enableSwaggerRefresh == null){
            super.enableSwaggerRefresh();
        }
        return enableSwaggerRefresh;
    }

    @Override
    public String version() {
        if(StringUtils.isNullOrEmpty(version)){
            return super.version();
        }
        return version;
    }

    @Override
    public boolean exactVersionAllowed() {
        if(exactVersionAllowed == null){
            return super.exactVersionAllowed();
        }
        return exactVersionAllowed;
    }

    @Override
    public boolean enableWebSocket() {
        if(enableWebSocket == null){
            return super.enableWebSocket();
        } else {
            return enableWebSocket;
        }
    }
}
