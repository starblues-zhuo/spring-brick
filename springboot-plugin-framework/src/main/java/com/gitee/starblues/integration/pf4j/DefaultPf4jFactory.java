package com.gitee.starblues.integration.pf4j;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 默认的插件集成工厂
 * @author zhangzhuo
 * @version 2.2.0
 */
public class DefaultPf4jFactory implements Pf4jFactory {

    private final IntegrationConfiguration configuration;

    public DefaultPf4jFactory(IntegrationConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    public PluginManager getPluginManager() {
        if(configuration == null){
            throw new NullPointerException("IntegrationConfiguration is null");
        }
        RuntimeMode environment = configuration.environment();
        if(environment == null){
            throw new RuntimeException("Configuration RuntimeMode is null" + configuration.environment());
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

                @Override
                protected PluginDescriptorFinder createPluginDescriptorFinder() {
                    return new CompoundPluginDescriptorFinder()
                            .add(new ResolvePropertiesPluginDescriptorFinder())
                            .add(new ManifestPluginDescriptorFinder());
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
            throw new RuntimeException("Not found run environment " + configuration.environment());
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
