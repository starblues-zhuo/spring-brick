package com.plugin.example.start.config;

import com.plugin.development.integration.*;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-25 12:36
 * @Update Date Time:
 * @see
 */
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginArgConfiguration implements IntegrationConfiguration {

    /**
     * 运行模式
     *  开发环境: development、dev
     *  生产/部署 环境: deployment、prod
     */
    @Value("${runMode:dev}")
    private String runMode;

    /**
     * 插件的路径
     */
    @Value("${pluginDir:plugins}")
    private String pluginDir;

    /**
     * 插件文件的路径
     */
    @Value("${pluginConfigFileDir:}")
    private String pluginConfigFileDir;


    @Override
    public RuntimeMode environment() {
        return RuntimeMode.byName(runMode);
    }

    @Override
    public String pluginDir() {
        return pluginDir;
    }

    @Override
    public String pluginConfigFileDir() {
        return pluginConfigFileDir;
    }


    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getPluginDir() {
        return pluginDir;
    }

    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    public String getPluginConfigFileDir() {
        return pluginConfigFileDir;
    }

    public void setPluginConfigFileDir(String pluginConfigFileDir) {
        this.pluginConfigFileDir = pluginConfigFileDir;
    }

    @Override
    public String toString() {
        return "PluginArgConfiguration{" +
                "runMode='" + runMode + '\'' +
                ", pluginDir='" + pluginDir + '\'' +
                ", pluginConfigFileDir='" + pluginConfigFileDir + '\'' +
                '}';
    }
}
