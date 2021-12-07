package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.*;
import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.DefaultIntegrationConfiguration;
import com.gitee.starblues.spring.PluginSpringApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.processor.SpringPluginProcessorFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
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
public class OneselfSpringApplication extends SpringApplication implements PluginSpringApplication{

    private final static Logger LOG = LoggerFactory.getLogger(OneselfSpringApplication.class);

    private final Class<?> primarySource;

    private final PluginWrapper pluginWrapper;

    private final SpringPluginProcessor springPluginProcessor;

    private SpringPluginRegistryInfo registryInfo;

    private GenericApplicationContext applicationContext;

    public OneselfSpringApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public OneselfSpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
        if(primarySources.length > 0){
            primarySource = primarySources[0];
        } else {
            primarySource = null;
        }
        // 运行之前, 从当前插件的 classpath 下获取插件引导信息
        pluginWrapper = tryGetPluginWrapper();
        this.springPluginProcessor = new SpringPluginProcessorFactory(SpringPluginProcessor.RunMode.ONESELF);
    }


    @Override
    public GenericApplicationContext run() throws Exception {
        return (GenericApplicationContext) super.run();
    }

    @Override
    public void close(){
        if(applicationContext != null){
            try {
                springPluginProcessor.close(registryInfo);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                applicationContext.close();
            }
        }
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        return applicationContext;
    }


    @Override
    protected void bindToSpringApplication(ConfigurableEnvironment environment) {
        super.bindToSpringApplication(environment);
        Map<String, Object> env = new HashMap<>();
        // 禁用插件的自动装配
        env.put(AutoIntegrationConfiguration.ENABLE_KEY, false);
        env.put(AutoIntegrationConfiguration.ENABLE_STARTER_KEY, false);
        String contextPath = getContextPath(environment);
        env.put("server.servlet.context-path", contextPath);
        LOG.info("当前应用接口前缀为: {}", contextPath);
        environment.getPropertySources().addFirst(new MapPropertySource("springPluginRegistryInfo", env));
    }

    @Override
    protected ConfigurableApplicationContext createApplicationContext() {
        ConfigurableApplicationContext applicationContext = super.createApplicationContext();
        try {
            this.applicationContext = (GenericApplicationContext) applicationContext;
            springPluginProcessor.init(this.applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationContext;
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        try {
            AutoIntegrationConfiguration configuration = bindIntegrationConfiguration(applicationContext);
            registryInfo = new OneselfSpringPluginRegistryInfo(pluginWrapper, this,
                    applicationContext, configuration);
            springPluginProcessor.refreshBefore(registryInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            super.refresh(applicationContext);
            springPluginProcessor.refreshAfter(registryInfo);
        } catch (Exception e){
            try {
                springPluginProcessor.failure(registryInfo);
            } catch (Exception exception) {
                throw new RuntimeException(e);
            }
        }

    }

    private AutoIntegrationConfiguration bindIntegrationConfiguration(
            ConfigurableApplicationContext applicationContext){
        Binder binder = Binder.get(applicationContext.getEnvironment());
        AutoIntegrationConfiguration configuration =
                binder.bind("plugin", Bindable.of(AutoIntegrationConfiguration.class))
                .orElseGet(() -> null);
        if(configuration == null){
            configuration = new AutoIntegrationConfiguration();
        }
        applicationContext.getBeanFactory().registerSingleton("integrationConfiguration",
                configuration);
        return configuration;
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
