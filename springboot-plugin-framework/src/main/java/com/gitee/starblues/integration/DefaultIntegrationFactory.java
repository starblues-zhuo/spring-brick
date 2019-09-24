package com.gitee.starblues.integration;

import org.pf4j.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 默认的插件集成工厂
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultIntegrationFactory implements IntegrationFactory {



    @Override
    public PluginManager getPluginManager(IntegrationConfiguration configuration) throws PluginException {
        RuntimeMode environment = configuration.environment();
        if(environment == null){
            throw new PluginException("Run environment can is null" + configuration.environment());
        }
        if(RuntimeMode.DEVELOPMENT == environment){
            // 开发环境下的插件管理者
            Path path = Paths.get(getDevPluginDir(configuration));
            return new DefaultPluginManager(path){
                @Override
                public RuntimeMode getRuntimeMode() {
                    System.setProperty("pf4j.mode", RuntimeMode.DEVELOPMENT.toString());
                    return RuntimeMode.DEVELOPMENT;
                }
            };
        } else if(RuntimeMode.DEPLOYMENT == environment){
            // 运行环境下的插件管理者
            Path path = Paths.get(getProdPluginDir(configuration));
            return new DefaultPluginManager(path){
                @Override
                protected PluginRepository createPluginRepository() {
                    return new CompoundPluginRepository()
                            .add(new JarPluginRepository(getPluginsRoot()));
                }
            };
        } else {
            throw new PluginException("Not found run environment " + configuration.environment());
        }
    }


    private String getDevPluginDir(IntegrationConfiguration configuration){
        String pluginDir = configuration.pluginPath();
        if(Objects.equals("", pluginDir)){
            pluginDir = "./plugins/";
        }
        return pluginDir;
    }


    private String getProdPluginDir(IntegrationConfiguration configuration){
        String pluginDir = configuration.pluginPath();
        if(Objects.equals("", pluginDir)){
            pluginDir = "plugins";
        }
        return pluginDir;
    }

}
