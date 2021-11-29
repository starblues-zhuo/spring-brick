package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.DefaultPluginManager;
import com.gitee.starblues.core.PluginManager;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.spring.SpringPlugin;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认的插件操作者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginOperator implements PluginOperator {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    private final GenericApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;

    private final SpringPlugin springPlugin;
    private final PluginManager pluginManager;

    public DefaultPluginOperator(GenericApplicationContext applicationContext,
                                 SpringPlugin springPlugin,
                                 RealizeProvider realizeProvider,
                                 IntegrationConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.springPlugin = springPlugin;
        this.pluginManager = new DefaultPluginManager(realizeProvider, configuration.pluginPath());
    }


    @Override
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws Exception {
        if(isInit.get()){
            throw new RuntimeException("插件已经被初始化了, 不能再初始化.");
        }
        try {
            //pluginInitializerListenerFactory.addPluginInitializerListeners(pluginInitializerListener);
            log.info("开始初始化加载插件: '{}'", pluginManager.getPluginsRoots().toString());
            // 触发插件初始化监听器
            //pluginInitializerListenerFactory.before();
            if(!configuration.enable()){
                // 如果禁用的话, 直接返回
                //pluginInitializerListenerFactory.complete();
                return false;
            }

            // 启动前, 清除空文件
            PluginFileUtils.cleanEmptyFile(pluginManager.getPluginsRoots());

            // 开始加载插件
            List<PluginDescriptor> pluginDescriptors = pluginManager.loadPlugins();
            if(ObjectUtils.isEmpty(pluginDescriptors)){
                //pluginInitializerListenerFactory.complete();
                return false;
            }
            boolean isFoundException = false;
            for (PluginDescriptor descriptor : pluginDescriptors) {
                try {
                    pluginManager.start(descriptor.getPluginId());
                    springPlugin.registry(pluginManager.getPluginWrapper(descriptor.getPluginId()));
                    log.info("启动插件[{}@{}]成功", descriptor.getPluginId(), descriptor.getPluginVersion());
                } catch (Exception e){
                    log.error("启动插件[{}]失败. {}", descriptor.getPluginId(), e.getMessage(), e);
                    isFoundException = true;
                }
            }
            isInit.set(true);
            if(isFoundException){
                log.error("插件初始化失败");
                //pluginInitializerListenerFactory.failure(new Exception("插件初始化失败"));
                return false;
            } else {
                log.info("插件初始化成功");
                //pluginInitializerListenerFactory.complete();
                return true;
            }
        }  catch (Exception e){
            //pluginInitializerListenerFactory.failure(e);
            throw e;
        }
    }

    @Override
    public boolean verify(Path jarPath) throws Exception {
        return false;
    }

    @Override
    public PluginInfo install(Path jarPath) throws Exception {
        return null;
    }

    @Override
    public boolean uninstall(String pluginId, boolean isBackup) throws Exception {
        return false;
    }

    @Override
    public PluginInfo load(Path jarPath) throws Exception {
        return null;
    }

    @Override
    public PluginInfo load(MultipartFile pluginFile) throws Exception {
        return null;
    }

    @Override
    public boolean unload(String pluginId, boolean isBackup) throws Exception {
        return false;
    }

    @Override
    public boolean start(String pluginId) throws Exception {
        return false;
    }

    @Override
    public boolean stop(String pluginId) throws Exception {
        return false;
    }

    @Override
    public PluginInfo uploadPluginAndStart(MultipartFile pluginFile) throws Exception {
        return null;
    }

    @Override
    public boolean installConfigFile(Path configFilePath) throws Exception {
        return false;
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws Exception {
        return false;
    }

    @Override
    public boolean backupPlugin(Path backDirPath, String sign) throws Exception {
        return false;
    }

    @Override
    public boolean backupPlugin(String pluginId, String sign) throws Exception {
        return false;
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        return null;
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        return null;
    }

    @Override
    public Set<String> getPluginFilePaths() throws Exception {
        return null;
    }

    @Override
    public List<PluginWrapper> getPluginWrapper() {
        return null;
    }

    @Override
    public PluginWrapper getPluginWrapper(String pluginId) {
        return null;
    }
}
