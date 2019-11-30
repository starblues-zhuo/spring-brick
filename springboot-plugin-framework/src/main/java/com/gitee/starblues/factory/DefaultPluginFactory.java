package com.gitee.starblues.factory;

import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorFactory;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.factory.process.post.PluginPostProcessorFactory;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.utils.AopUtils;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的插件处理者
 *
 * @author zhangzhuo
 * @version 2.2.2
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
        this.pluginPipeProcessor = new PluginPipeProcessorFactory(applicationContext);
        this.pluginPostProcessor = new PluginPostProcessorFactory(applicationContext);
        this.applicationContext = (GenericApplicationContext) applicationContext;
        if(pluginListenerFactory == null){
            this.pluginListenerFactory = new PluginListenerFactory();
        } else {
            this.pluginListenerFactory = pluginListenerFactory;
        }
        AopUtils.registered(applicationContext);
    }


    @Override
    public void initialize() throws Exception{
        pluginPipeProcessor.initialize();
        pluginPostProcessor.initialize();
    }

    @Override
    public synchronized PluginFactory registry(PluginWrapper pluginWrapper) throws Exception {
        if(pluginWrapper == null){
            throw new IllegalArgumentException("Parameter:pluginWrapper cannot be null");
        }
        if(registerPluginInfoMap.containsKey(pluginWrapper.getPluginId())){
            throw new IllegalAccessException("The plugin '"
                    + pluginWrapper.getPluginId() +"' already exists, Can't register");
        }
        if(!buildContainer.isEmpty() && buildType == 2){
            throw new IllegalAccessException("Unable to Registry operate. Because there's no build");
        }
        PluginRegistryInfo registerPluginInfo = new PluginRegistryInfo(pluginWrapper);
        AopUtils.resolveAop(pluginWrapper);
        try {
            pluginPipeProcessor.registry(registerPluginInfo);
            registerPluginInfoMap.put(pluginWrapper.getPluginId(), registerPluginInfo);
            buildContainer.add(registerPluginInfo);
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
            throw new Exception("No Found registered or unRegistry plugin. Unable to build");
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
            buildContainer.clear();
            if(buildType == 1){
                AopUtils.recoverAop();
            }
            buildType = 0;
        }
    }

    /**
     * 注册build
     */
    private void registryBuild() throws Exception {
        pluginPostProcessor.registry(buildContainer);
        for (PluginRegistryInfo pluginRegistryInfo : buildContainer) {
            pluginListenerFactory.registry(pluginRegistryInfo.getPluginWrapper().getPluginId());
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




}
