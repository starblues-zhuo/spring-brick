package com.mybatis.main.config;

import com.gitee.starblues.extension.mybatis.SpringBootMybatisExtension;
import com.gitee.starblues.extension.resources.StaticResourceExtension;
import com.gitee.starblues.integration.*;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import com.google.common.collect.Sets;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 插件集成配置
 * @Author: starBlues
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 15:53
 * @Update Date Time:
 * @see
 */
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginBeanConfig {

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
    @Value("${pluginPath:plugins}")
    private String pluginPath;

    /**
     * 插件文件的路径
     */
    @Value("${pluginConfigFilePath:pluginConfigs}")
    private String pluginConfigFilePath;



    @Bean
    public IntegrationConfiguration configuration(){
        return ConfigurationBuilder.toBuilder()
                .runtimeMode(RuntimeMode.byName(runMode))
                .pluginPath(pluginPath)
                .pluginConfigFilePath(pluginConfigFilePath)
                .uploadTempPath("temp")
                .backupPath("backupPlugin")
                .pluginRestPathPrefix("/api/plugin")
                .enablePluginIdRestPathPrefix(true)
                .enableSwaggerRefresh(true)
                .enablePluginIds(Sets.newHashSet("integration-mybatis-plugin1"))
                .disablePluginIds(Sets.newHashSet("integration-mybatis-plugin2"))
                .build();
    }


    /**
     * 定义插件应用。使用可以注入它操作插件。
     * @return PluginApplication
     */
    @Bean
    public PluginApplication pluginApplication(){
        // 实例化自动初始化插件的PluginApplication
        PluginApplication pluginApplication = new AutoPluginApplication();
        pluginApplication.addExtension(new SpringBootMybatisExtension(
                SpringBootMybatisExtension.Type.MYBATIS
        ));
        // 新增静态资源扩展
        StaticResourceExtension staticResourceExtension = new StaticResourceExtension(
                StaticResourceExtension.Include.THYMELEAF);
        staticResourceExtension.setPathPrefix("static");
        staticResourceExtension.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
        pluginApplication.addExtension(staticResourceExtension);
        return pluginApplication;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public void setPluginConfigFilePath(String pluginConfigFilePath) {
        this.pluginConfigFilePath = pluginConfigFilePath;
    }
}
