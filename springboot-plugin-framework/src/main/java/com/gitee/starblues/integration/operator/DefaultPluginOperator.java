package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.*;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 默认的插件操作者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginOperator implements PluginOperator {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    private final IntegrationConfiguration configuration;

    private final PluginManager pluginManager;

    public DefaultPluginOperator(GenericApplicationContext applicationContext,
                                 RealizeProvider realizeProvider,
                                 IntegrationConfiguration configuration) {
        this.configuration = configuration;
        this.pluginManager = new PluginLauncherManager(realizeProvider, applicationContext, configuration);
    }


    @Override
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException {
        if(isInit.get()){
            throw new RuntimeException("插件已经被初始化了, 不能再初始化.");
        }
        try {
            //pluginInitializerListenerFactory.addPluginInitializerListeners(pluginInitializerListener);
            log.info("开始初始化加载插件: '{}'", pluginManager.getPluginsRoots().toString());
            // 触发插件初始化监听器
            //pluginInitializerListenerFactory.before();
            if(!configuration.enable()){
                log.info("插件功能已被禁用!");
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
                } catch (PluginException e){
                    log.error(e.getMessage(), e);
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
    public boolean verify(Path jarPath) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo install(Path jarPath) throws PluginException {
        return null;
    }

    @Override
    public boolean uninstall(String pluginId, boolean isBackup) throws PluginException {
        pluginManager.uninstall(pluginId);
        return true;
    }

    @Override
    public PluginInfo load(Path jarPath) throws PluginException {
        return null;
    }

    @Override
    public PluginInfo load(MultipartFile pluginFile) throws PluginException {
        return null;
    }

    @Override
    public boolean unload(String pluginId, boolean isBackup) throws PluginException {
        return false;
    }

    @Override
    public boolean start(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public boolean stop(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo uploadPluginAndStart(MultipartFile pluginFile) throws PluginException {
        return null;
    }

    @Override
    public boolean installConfigFile(Path configFilePath) throws PluginException {
        return false;
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws PluginException {
        return false;
    }

    @Override
    public boolean backupPlugin(Path backDirPath, String sign) throws PluginException {
        return false;
    }

    @Override
    public boolean backupPlugin(String pluginId, String sign) throws PluginException {
        return false;
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        List<PluginWrapper> startedPlugins = pluginManager.getPluginWrappers();
        List<PluginInfo> pluginInfos = new ArrayList<>();
        if(startedPlugins == null){
            return pluginInfos;
        }
        return startedPlugins.stream()
                .filter(Objects::nonNull)
                .map(this::getPluginInfo)
                .collect(Collectors.toList());
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        return null;
    }

    @Override
    public Set<String> getPluginFilePaths() {
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


    /**
     * 通过PluginWrapper得到插件信息
     * @param pluginWrapper pluginWrapper
     * @return PluginInfo
     */
    private PluginInfo getPluginInfo(PluginWrapper pluginWrapper) {
        return new PluginInfo(pluginWrapper.getPluginDescriptor(), pluginWrapper.getPluginState(),
                pluginWrapper.getPluginPath().toAbsolutePath().toString(),
                configuration.environment().toString());
    }
}
