package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.*;
import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.spring.DefaultSpringPluginRegistryInfo;
import com.gitee.starblues.spring.PluginSpringApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.BeforeRefreshProcessorFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
public class OneselfSpringApplication extends SpringApplication {

    private final Class<?> primarySource;
    private final PluginSpringApplication springApplication;

    public OneselfSpringApplication(PluginSpringApplication springApplication, Class<?>... primarySources) {
        this(springApplication, null, primarySources);
    }

    public OneselfSpringApplication(PluginSpringApplication springApplication,
                                    ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
        this.springApplication = springApplication;
        if(primarySources.length > 0){
            primarySource = primarySources[0];
        } else {
            primarySource = null;
        }
    }

    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
        Map<String, Object> environmentMap = new HashMap<>();
        environmentMap.put(AutoIntegrationConfiguration.ENABLE_KEY, false);
        MapPropertySource mapPropertySource = new MapPropertySource("plugin-environment", environmentMap);
        environment.getPropertySources().addLast(mapPropertySource);
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        // 刷新之前
        BeforeRefreshProcessorFactory beforeRefreshProcessorFactory =
                new BeforeRefreshProcessorFactory(null);
        SpringPluginRegistryInfo springPluginRegistryInfo = create(applicationContext);
        beforeRefreshProcessorFactory.registry(springPluginRegistryInfo);
        super.refresh(applicationContext);
    }

    private SpringPluginRegistryInfo create(ConfigurableApplicationContext applicationContext){
        return new DefaultSpringPluginRegistryInfo(tryGetPluginWrapper(), new PluginSpringApplication() {
            @Override
            public ConfigurableApplicationContext run() {
                return springApplication.run();
            }

            @Override
            public void close() {
                springApplication.close();
            }

            @Override
            public ConfigurableApplicationContext getApplicationContext() {
                return applicationContext;
            }
        }, applicationContext);
    }

    private PluginWrapper tryGetPluginWrapper(){
        try {
            PluginDescriptorLoader pluginDescriptorLoader = new DevPluginDescriptorLoader();
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(Paths.get(this.getClass().getResource("/").toURI()));
            if(pluginDescriptor == null){
                return getPluginWrapper(new EmptyPluginDescriptor());
            } else {
                return getPluginWrapper(pluginDescriptor);
            }
        } catch (Exception e){
            return getPluginWrapper(new EmptyPluginDescriptor());
        }
    }

    private PluginWrapper getPluginWrapper(PluginDescriptor pluginDescriptor){
        return new PluginWrapper() {
            @Override
            public String getPluginId() {
                return pluginDescriptor.getPluginId();
            }

            @Override
            public PluginDescriptor getPluginDescriptor() {
                return pluginDescriptor;
            }

            @Override
            public ClassLoader getPluginClassLoader() {
                return this.getClass().getClassLoader();
            }

            @Override
            public Class<?> getPluginClass() {
                return primarySource;
            }

            @Override
            public Path getPluginPath() {
                return pluginDescriptor.getPluginPath();
            }

            @Override
            public PluginState getPluginState() {
                return PluginState.STARTED;
            }
        };
    }

}
