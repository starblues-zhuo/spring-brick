package com.gitee.starblues.factory;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorFactory;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.factory.process.post.PluginPostProcessorFactory;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.listener.SwaggerListeningListener;
import com.gitee.starblues.utils.AopUtils;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的插件处理者
 *
 * @author starBlues
 * @version 2.4.0
 */
public class DefaultPluginFactory implements PluginFactory {


    /**
     * 注册的插件集合
     */
    private final Map<String, PluginRegistryInfo> registerPluginInfoMap = new HashMap<>();
    private final GenericApplicationContext applicationContext;
    private final PluginPipeProcessor pluginPipeProcessor;
    private final PluginPostProcessor pluginPostProcessor;
    private final PluginListenerFactory pluginListenerFactory;
    private final IntegrationConfiguration configuration;

    /**
     *  0表示build、1 表示注册、2表示卸载
     */
    private Integer buildType = 0;
    private final List<PluginRegistryInfo> buildContainer = new ArrayList<>();

    public DefaultPluginFactory(ApplicationContext applicationContext) {
        this(applicationContext, null);
    }


    public DefaultPluginFactory(ApplicationContext applicationContext,
                                PluginListenerFactory pluginListenerFactory) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.pluginPipeProcessor = new PluginPipeProcessorFactory(applicationContext);
        this.pluginPostProcessor = new PluginPostProcessorFactory(applicationContext);

        if(pluginListenerFactory == null){
            this.pluginListenerFactory = new PluginListenerFactory();
        } else {
            this.pluginListenerFactory = pluginListenerFactory;
        }
        configuration = applicationContext.getBean(IntegrationConfiguration.class);
        AopUtils.registered(applicationContext);
    }


    @Override
    public void initialize() throws Exception{
        // 新增默认监听者
        addDefaultPluginListener();
        pluginPipeProcessor.initialize();
        pluginPostProcessor.initialize();
    }

    @Override
    public synchronized PluginFactory registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(pluginRegistryInfo == null){
            throw new IllegalArgumentException("Parameter:pluginRegistryInfo cannot be null");
        }
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        if(registerPluginInfoMap.containsKey(pluginWrapper.getPluginId())){
            throw new IllegalAccessException("The plugin '"
                    + pluginWrapper.getPluginId() +"' already exists, Can't register");
        }
        if(!buildContainer.isEmpty() && buildType == 2){
            throw new IllegalAccessException("Unable to Registry operate. Because there's no build");
        }
        AopUtils.resolveAop(pluginWrapper);
        try {
            pluginPipeProcessor.registry(pluginRegistryInfo);
            registerPluginInfoMap.put(pluginWrapper.getPluginId(), pluginRegistryInfo);
            buildContainer.add(pluginRegistryInfo);
            return this;
        } catch (Exception e) {
            pluginListenerFactory.failure(pluginWrapper.getPluginId(), e);
            throw e;
        } finally {
            buildType = 1;
            AopUtils.recoverAop();
        }
    }

    @Override
    public synchronized PluginFactory unRegistry(String pluginId) throws Exception {
        PluginRegistryInfo registerPluginInfo = registerPluginInfoMap.get(pluginId);
        if(registerPluginInfo == null){
            throw new Exception("Not found plugin '" + pluginId + "' registered");
        }
        if(!buildContainer.isEmpty() && buildType == 1){
            throw new Exception("Unable to UnRegistry operate. Because there's no build");
        }
        try {
            pluginPipeProcessor.unRegistry(registerPluginInfo);
            buildContainer.add(registerPluginInfo);
            return this;
        } catch (Exception e) {
            pluginListenerFactory.failure(pluginId, e);
            throw e;
        } finally {
            registerPluginInfoMap.remove(pluginId);
            buildType = 2;
        }
    }



    @Override
    public synchronized void build() throws Exception {
        if(buildContainer.isEmpty()){
            return;
        }
        // 构建注册的Class插件监听者
        pluginListenerFactory.buildListenerClass(applicationContext);
        try {
            if(buildType == 1){
                registryBuild();
            } else {
                unRegistryBuild();
            }
        } finally {
            if(buildType == 1){
                AopUtils.recoverAop();
            } else {
                for (PluginRegistryInfo pluginRegistryInfo : buildContainer) {
                    // 卸载classLoader
                    closeClassLoader(pluginRegistryInfo);
                    pluginRegistryInfo.clear();
                }
            }
            buildContainer.clear();
            buildType = 0;
        }
    }



    @Override
    public void addListener(PluginListener pluginListener) {
        pluginListenerFactory.addPluginListener(pluginListener);
    }

    @Override
    public <T extends PluginListener> void addListener(Class<T> pluginListenerClass) {
        pluginListenerFactory.addPluginListener(pluginListenerClass);
    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {
        if(pluginListeners != null){
            for (PluginListener pluginListener : pluginListeners) {
                pluginListenerFactory.addPluginListener(pluginListener);
            }
        }
    }

    /**
     * 注册build
     */
    private void registryBuild() throws Exception {
        pluginPostProcessor.registry(buildContainer);
        for (PluginRegistryInfo pluginRegistryInfo : buildContainer) {
            pluginListenerFactory.registry(
                    pluginRegistryInfo.getPluginWrapper().getPluginId(),
                    pluginRegistryInfo.isFollowingInitial());
        }
    }

    /**
     * 卸载build
     */
    private void unRegistryBuild() throws Exception {
        pluginPostProcessor.unRegistry(buildContainer);
        for (PluginRegistryInfo pluginRegistryInfo : buildContainer) {
            pluginListenerFactory.unRegistry(pluginRegistryInfo.getPluginWrapper().getPluginId());
        }
    }

    /**
     * 卸载Close Loader
     * @param registerPluginInfo registerPluginInfo
     */
    private void closeClassLoader(PluginRegistryInfo registerPluginInfo) {
        List<ClassLoader> pluginClassLoaders = registerPluginInfo.getPluginClassLoaders();
        for (ClassLoader pluginClassLoader : pluginClassLoaders) {
            if (pluginClassLoader instanceof Closeable) {
                try {
                    ((Closeable) pluginClassLoader).close();
                } catch (IOException e) {
                    throw new PluginRuntimeException(e, "");
                }
            }
        }
    }

    /**
     * 添加默认插件监听者
     */
    private void addDefaultPluginListener(){
        if(configuration.enableSwaggerRefresh()){
            pluginListenerFactory.addPluginListener(new SwaggerListeningListener(applicationContext));
        }
    }


}
