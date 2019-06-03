package com.plugin.development.integration;

import org.pf4j.*;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-26 19:34
 * @Update Date Time:
 * @see
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
            System.setProperty("pf4j.pluginsDir", getDevPluginDir(configuration));
            return new DefaultPluginManager(){
                @Override
                public RuntimeMode getRuntimeMode() {
                    System.setProperty("pf4j.mode", RuntimeMode.DEVELOPMENT.toString());
                    return RuntimeMode.DEVELOPMENT;
                }
            };
        } else if(RuntimeMode.DEPLOYMENT == environment){
            // 运行环境下的插件管理者
            return new DefaultPluginManager(Paths.get(getProdPluginDir(configuration))){
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
