package com.gitee.starblues.integration.pf4j;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 默认的插件集成工厂
 * @author starBlues
 * @version 2.4.0
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
        List<String> sortInitPluginIds = configuration.sortInitPluginIds();
        DefaultPluginManager defaultPluginManager = null;
        if(RuntimeMode.DEVELOPMENT == environment){
            // 开发环境下的插件管理者
            Path path = Paths.get(getDevPluginDir(configuration));
            defaultPluginManager = new DefaultPluginManager(path){

                @Override
                protected void initialize() {
                    super.initialize();
                    dependencyResolver = new SortDependencyResolver(sortInitPluginIds, versionManager);
                }

                @Override
                public RuntimeMode getRuntimeMode() {
                    System.setProperty("pf4j.mode", RuntimeMode.DEVELOPMENT.toString());
                    return RuntimeMode.DEVELOPMENT;
                }

                @Override
                protected PluginDescriptorFinder createPluginDescriptorFinder() {
                    return DefaultPf4jFactory.getPluginDescriptorFinder(RuntimeMode.DEVELOPMENT);
                }

                @Override
                protected PluginLoader createPluginLoader() {
                    return new CompoundPluginLoader()
                            .add(new DevelopmentPluginLoader(this), this::isDevelopment);
                }

                @Override
                protected PluginStatusProvider createPluginStatusProvider() {
                    return new ConfigPluginStatusProvider(
                            configuration.enablePluginIds(),
                            configuration.disablePluginIds());
                }

            };
        } else if(RuntimeMode.DEPLOYMENT == environment){
            // 运行环境下的插件管理者
            Path path = Paths.get(getProdPluginDir(configuration));
            defaultPluginManager = new DefaultPluginManager(path){

                @Override
                protected void initialize() {
                    super.initialize();
                    dependencyResolver = new SortDependencyResolver(sortInitPluginIds, versionManager);
                }

                @Override
                protected PluginDescriptorFinder createPluginDescriptorFinder() {
                    return DefaultPf4jFactory.getPluginDescriptorFinder(RuntimeMode.DEPLOYMENT);
                }

                @Override
                protected PluginStatusProvider createPluginStatusProvider() {
                    return new ConfigPluginStatusProvider(
                            configuration.enablePluginIds(),
                            configuration.disablePluginIds());
                }

                @Override
                protected PluginLoader createPluginLoader() {
                    return new CompoundPluginLoader()
                            .add(new JarPluginLoader(this), this::isNotDevelopment)
                            .add(new DefaultPluginLoader(this), this::isNotDevelopment);
                }

            };
        }
        if(defaultPluginManager == null){
            throw new RuntimeException("Not found run environment " + configuration.environment());
        }
        defaultPluginManager.setSystemVersion(configuration.version());
        defaultPluginManager.setExactVersionAllowed(configuration.exactVersionAllowed());
        return defaultPluginManager;
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

    public static PluginDescriptorFinder getPluginDescriptorFinder(RuntimeMode runtimeMode){
        if(runtimeMode == RuntimeMode.DEPLOYMENT){
            // 生产
            return new CompoundPluginDescriptorFinder()
                    .add(new ResourcesPluginDescriptorFinder(RuntimeMode.DEPLOYMENT))
                    .add(new ManifestPluginDescriptorFinder());
        } else {
            // 开发
            return new CompoundPluginDescriptorFinder()
                    .add(new ResourcesPluginDescriptorFinder(RuntimeMode.DEVELOPMENT))
                    .add(new ResolvePropertiesPluginDescriptorFinder())
                    .add(new ManifestPluginDescriptorFinder());
        }
    }

}
