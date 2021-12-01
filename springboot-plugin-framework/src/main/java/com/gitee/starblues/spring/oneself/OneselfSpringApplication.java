package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.*;
import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.DefaultIntegrationConfiguration;
import com.gitee.starblues.spring.DefaultSpringPluginRegistryInfo;
import com.gitee.starblues.spring.PluginSpringApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.BeforeRefreshProcessorFactory;
import com.gitee.starblues.utils.ObjectUtils;
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
 * 插件自主运行时的SpringApplication环境, 对  SpringApplication 进行再次封装
 * @author starBlues
 * @version 3.0.0
 * @see SpringApplication
 */
public class OneselfSpringApplication extends SpringApplication {

    private final Class<?> primarySource;
    private final PluginSpringApplication springApplication;

    private final PluginWrapper pluginWrapper;

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
        // 运行之前, 从当前插件的 classpath 下获取插件引导信息
        pluginWrapper = tryGetPluginWrapper();
    }

    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
        super.configureProfiles(environment, args);
    }

    @Override
    protected void bindToSpringApplication(ConfigurableEnvironment environment) {
        super.bindToSpringApplication(environment);
        Map<String, Object> env = new HashMap<>();
        // 禁用插件的自动装配
        env.put(AutoIntegrationConfiguration.ENABLE_KEY, false);
        env.put("server.servlet.context-path", getContextPath(environment));
        environment.getPropertySources().addFirst(new MapPropertySource("springPluginRegistryInfo", env));
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        // 刷新之前
        BeforeRefreshProcessorFactory beforeRefreshProcessorFactory =
                new BeforeRefreshProcessorFactory(null);
        SpringPluginRegistryInfo springPluginRegistryInfo = create(applicationContext);
        beforeRefreshProcessorFactory.registryOfBefore(springPluginRegistryInfo);
        super.refresh(applicationContext);
    }

    private SpringPluginRegistryInfo create(ConfigurableApplicationContext applicationContext){
        return new DefaultSpringPluginRegistryInfo(pluginWrapper, new PluginSpringApplication() {
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

    /**
     * 尝试从 classpath 获取插件引导信息, 如果不存在, 则生成空的 PluginWrapper
     * @return PluginWrapper
     */
    private PluginWrapper tryGetPluginWrapper(){
        try {
            PluginDescriptorLoader pluginDescriptorLoader = new DevPluginDescriptorLoader();
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(
                    Paths.get(this.getClass().getResource("/").toURI()));
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

    /**
     * 获取独立运行插件的接口 url 前缀
     * @param environment environment
     * @return String
     */
    private String getContextPath(ConfigurableEnvironment environment){
        String pluginRestPathPrefix = environment.getProperty("plugin.pluginRestPathPrefix", String.class);
        Boolean enablePluginIdRestPathPrefix = environment.getProperty("plugin.enablePluginIdRestPathPrefix",
                Boolean.class);
        String contextPath = "";
        if(ObjectUtils.isEmpty(pluginRestPathPrefix)){
           pluginRestPathPrefix = DefaultIntegrationConfiguration.DEFAULT_PLUGIN_REST_PATH_PREFIX;
        }
        if(enablePluginIdRestPathPrefix == null){
            enablePluginIdRestPathPrefix = DefaultIntegrationConfiguration.DEFAULT_ENABLE_PLUGIN_ID_REST_PATH_PREFIX;
        }
        String pluginId = pluginWrapper.getPluginId();
        contextPath = "/" + pluginRestPathPrefix;
        if(enablePluginIdRestPathPrefix && !ObjectUtils.isEmpty(pluginId)){
            contextPath = contextPath + "/" + pluginId;
        }
        return contextPath;
    }

}
