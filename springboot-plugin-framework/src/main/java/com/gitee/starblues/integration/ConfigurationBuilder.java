package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * 通过构造者进行配置插件初始化配置
 *
 * @author starBlues
 * @version 2.3.1
 */
public class ConfigurationBuilder extends DefaultIntegrationConfiguration{

    private boolean enable;

    private RuntimeMode runtimeMode = RuntimeMode.DEVELOPMENT;
    private String pluginPath = "";
    private String pluginConfigFilePath = "";

    private String uploadTempPath;
    private String backupPath;
    private String pluginRestControllerPathPrefix;
    private Boolean enablePluginIdRestControllerPathPrefix;

    private Set<String> disablePluginIds;

    public ConfigurationBuilder(Builder builder) {
        this.runtimeMode = Objects.requireNonNull(builder.runtimeMode, "runtimeMode can't be empty");
        this.pluginPath = Objects.requireNonNull(builder.pluginPath, "pluginPath can't be empty");
        this.pluginConfigFilePath = Objects.requireNonNull(builder.pluginConfigFilePath,
                "pluginConfigFilePath can't be empty");
        this.uploadTempPath = builder.uploadTempPath;
        this.backupPath = builder.backupPath;
        this.pluginRestControllerPathPrefix = builder.pluginRestControllerPathPrefix;
        this.enablePluginIdRestControllerPathPrefix = builder.enablePluginIdRestControllerPathPrefix;
        this.disablePluginIds = builder.disablePluginIds;
        if(builder.enable == null){
            this.enable = true;
        } else {
            this.enable = builder.enable;
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
        private String pluginRestControllerPathPrefix;
        private Boolean enablePluginIdRestControllerPathPrefix;

        private Set<String> disablePluginIds;

        public Builder runtimeMode(RuntimeMode runtimeMode){
            this.runtimeMode = runtimeMode;
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

        public Builder pluginRestControllerPathPrefix(String pluginRestControllerPathPrefix){
            this.pluginRestControllerPathPrefix = pluginRestControllerPathPrefix;
            return this;
        }

        public Builder enablePluginIdRestControllerPathPrefix(Boolean enablePluginIdRestControllerPathPrefix){
            this.enablePluginIdRestControllerPathPrefix = enablePluginIdRestControllerPathPrefix;
            return this;
        }

        public Builder disablePluginIds(Set<String> disablePluginIds){
            this.disablePluginIds = disablePluginIds;
            return this;
        }

        public Builder enable(Boolean enable){
            this.enable = enable;
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
        if(StringUtils.isEmpty(uploadTempPath)){
            return super.uploadTempPath();
        } else {
            return uploadTempPath;
        }
    }

    @Override
    public String backupPath() {
        if(StringUtils.isEmpty(backupPath)){
            return super.backupPath();
        } else {
            return backupPath;
        }
    }

    @Override
    public String pluginRestControllerPathPrefix() {
        if(StringUtils.isEmpty(pluginRestControllerPathPrefix)){
            return super.pluginRestControllerPathPrefix();
        } else {
            return pluginRestControllerPathPrefix;
        }
    }

    @Override
    public boolean enablePluginIdRestControllerPathPrefix() {
        if(enablePluginIdRestControllerPathPrefix == null){
            return super.enablePluginIdRestControllerPathPrefix();
        } else {
            return enablePluginIdRestControllerPathPrefix;
        }
    }

    @Override
    public boolean enable() {
        return enable;
    }

    @Override
    public Set<String> disablePluginIds() {
        return disablePluginIds;
    }
}
