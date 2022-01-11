package com.gitee.starblues.core;

import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 抽象的插件管理者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginManager implements PluginManager{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RealizeProvider provider;
    private final List<String> pluginRootDirs;

    private PluginConfiguration configuration = new PluginConfiguration();

    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private final Map<String, PluginWrapperInside> startedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginDescriptor> resolvedPlugins = new ConcurrentHashMap<>();

    public DefaultPluginManager(RealizeProvider realizeProvider, String ...pluginRootDirs) {
        this(realizeProvider, pluginRootDirs == null ? null : Arrays.asList(pluginRootDirs));
    }

    public DefaultPluginManager(RealizeProvider realizeProvider, List<String> pluginRootDirs) {
        this.provider = Assert.isNotNull(realizeProvider,
                "参数 realizeProvider 不能为空");
        if(pluginRootDirs == null){
            this.pluginRootDirs = Collections.emptyList();
        } else {
            this.pluginRootDirs = pluginRootDirs;
        }
    }

    public void setConfiguration(PluginConfiguration configuration) {
        this.configuration = Assert.isNotNull(configuration, "参数 configuration 不能为空");
    }

    @Override
    public List<Path> getPluginsRoots() {
        return pluginRootDirs.stream()
                .filter(Objects::nonNull)
                .map(Paths::get)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<PluginDescriptor> loadPlugins() {
        if(loaded.get()){
            throw new RuntimeException("已经加载过了插件, 不能在重复调用: loadPlugins");
        }
        try {
            if(ObjectUtils.isEmpty(pluginRootDirs)){
                log.warn("插件根目录为空, 无法发现插件.");
                return Collections.emptyList();
            }
            List<Path> scanPluginPaths = provider.getPluginScanner().scan(pluginRootDirs);
            if(ObjectUtils.isEmpty(scanPluginPaths)){
                StringBuilder warn = new StringBuilder("\n\n路径 {} 中未发现插件.\n");
                warn.append("请检查路径是否合适.\n");
                if(provider.getRuntimeMode() == RuntimeMode.DEV){
                    warn.append("请检查插件包是否编译.\n");
                }
                log.warn(warn.toString(), pluginRootDirs);
                return Collections.emptyList();
            }
            List<PluginDescriptor> result = new ArrayList<>(scanPluginPaths.size());
            for (Path path : scanPluginPaths) {
                try {
                    PluginDescriptor descriptor = load(path);
                    if(descriptor != null){
                        result.add(descriptor);
                    }
                } catch (PluginException e) {
                    log.error("从路径[{}]加载插件失败. {}", path, e.getMessage(), e);
                }
            }
            return result;
        } finally {
            loaded.set(true);
        }
    }

    @Override
    public boolean verify(Path jarPath) {
        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            provider.getPluginChecker().check(jarPath);
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(jarPath);
            if(pluginDescriptor == null){
                return false;
            }
            provider.getPluginChecker().check(pluginDescriptor);
            return true;
        } catch (Exception e) {
            log.error("插件jar包校验失败. [{}]" , jarPath, e);
            return false;
        }
    }

    @Override
    public synchronized PluginDescriptor load(Path pluginPath) throws PluginException {
        try {
            provider.getPluginChecker().check(pluginPath);
        } catch (Exception e) {
            throw new PluginException("非法插件: " + pluginPath, e);
        }
        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            if(pluginDescriptor == null){
                return null;
            }
            String pluginId = pluginDescriptor.getPluginId();
            if(resolvedPlugins.containsKey(pluginId) || startedPlugins.containsKey(pluginId)){
                log.error("已经存在插件: {}", pluginId);
                return null;
            }
            resolvedPlugins.put(pluginDescriptor.getPluginId(), pluginDescriptor);
            log.info("加载插件[{}]成功", MsgUtils.getPluginUnique(pluginDescriptor));
            return pluginDescriptor;
        } catch (Exception e){
            throw new PluginException("加载插件失败");
        }

    }

    @Override
    public synchronized void unLoad(String pluginId) {
        resolvedPlugins.remove(pluginId);
    }

    @Override
    public synchronized PluginDescriptor install(Path pluginPath) throws PluginException {
        PluginDescriptor pluginDescriptor = load(pluginPath);
        if(pluginDescriptor == null){
            throw new PluginException("安装[" + pluginPath + "]插件包失败.");
        }
        try {
            log.info("安装插件[{}]成功", MsgUtils.getPluginUnique(pluginDescriptor));
            return start(pluginDescriptor);
        } catch (Exception e){
            unLoad(pluginDescriptor.getPluginId());
            throw new PluginException("安装启动[" + pluginPath + "]插件包失败.");
        }
    }

    @Override
    public synchronized void uninstall(String pluginId) throws PluginException {
        PluginWrapperInside pluginWrapper = startedPlugins.get(pluginId);
        PluginDescriptor pluginDescriptor = resolvedPlugins.get(pluginId);
        if(pluginWrapper == null && pluginDescriptor == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        if(pluginWrapper != null){
            if(pluginWrapper.getPluginState() == PluginState.STARTED){
                try {
                    stop(pluginWrapper);
                } catch (Exception e) {
                    throw new PluginException("停止", pluginId, e);
                }
            }
            startedPlugins.remove(pluginId);
            pluginDescriptor = pluginWrapper.getPluginDescriptor();
        }
        resolvedPlugins.remove(pluginId);
        System.gc();
        log.info("卸载插件[{}]成功", MsgUtils.getPluginUnique(pluginDescriptor));
    }

    @Override
    public synchronized void upgrade(Path pluginPath) throws PluginException {
        PluginDescriptor upgradePluginDescriptor = load(pluginPath);
        if(upgradePluginDescriptor == null){
            throw new PluginException("解析[" + pluginPath + "]插件更新包失败.");
        }
        String pluginId = upgradePluginDescriptor.getPluginId();
        PluginWrapper pluginWrapper = startedPlugins.get(pluginId);
        PluginDescriptor pluginDescriptor = resolvedPlugins.get(pluginId);
        if(pluginWrapper == null && pluginDescriptor == null){
            throw new PluginException("没有发现插件: " + pluginId + ", 无法更新.");
        }
        String oldPluginVersion;
        if(pluginDescriptor != null){
            oldPluginVersion = pluginDescriptor.getPluginVersion();
        } else {
            oldPluginVersion = pluginWrapper.getPluginDescriptor().getPluginVersion();
        }
        int compareVersion = provider.getVersionInspector().compareTo(oldPluginVersion,
                upgradePluginDescriptor.getPluginVersion());
        if(compareVersion < 0){
            throw new PluginException("更新插件包[" + upgradePluginDescriptor.getPluginVersion() + "]版本不能小于等于:"
                    + oldPluginVersion);
        }
        if(pluginDescriptor != null){
            // 说明插件没有被启动
            resolvedPlugins.put(pluginId, upgradePluginDescriptor);
        } else {
            uninstall(pluginId);
        }
        try {
            start(upgradePluginDescriptor);
            log.info("更新插件[{}]成功", MsgUtils.getPluginUnique(upgradePluginDescriptor));
        } catch (Exception e){
            throw new PluginException("更新启动", pluginId, e);
        }
    }

    @Override
    public synchronized PluginDescriptor start(String pluginId) throws PluginException {
        PluginDescriptor pluginDescriptor = resolvedPlugins.get(pluginId);
        if(pluginDescriptor == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        try {
            start(pluginDescriptor);
            log.info("启动插件[{}]成功", MsgUtils.getPluginUnique(pluginDescriptor));
            return pluginDescriptor;
        } catch (Exception e){
            throw new PluginException("启动", pluginId, e);
        }
    }

    @Override
    public synchronized PluginDescriptor stop(String pluginId) throws PluginException {
        PluginWrapperInside pluginWrapper = startedPlugins.get(pluginId);
        if(pluginWrapper == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        try {
            stop(pluginWrapper);
            log.info("停止插件[{}]成功", MsgUtils.getPluginUnique(pluginWrapper.getPluginDescriptor()));
            return pluginWrapper.getPluginDescriptor();
        } catch (Exception e) {
            throw new PluginException("停止插件 [" + pluginId + "] 失败. ", e);
        }
    }

    @Override
    public List<PluginDescriptor> getPluginDescriptors() {
        List<PluginDescriptor> pluginDescriptors = new ArrayList<>(resolvedPlugins.values());
        for (PluginWrapper value : startedPlugins.values()) {
            pluginDescriptors.add(value.getPluginDescriptor());
        }
        return pluginDescriptors;
    }

    @Override
    public PluginDescriptor getPluginDescriptor(String pluginId) {
        PluginWrapper pluginWrapper = startedPlugins.get(pluginId);
        if(pluginWrapper != null){
            return pluginWrapper.getPluginDescriptor();
        }
        return resolvedPlugins.get(pluginId);
    }

    @Override
    public List<PluginWrapper> getPluginWrappers() {
        List<PluginWrapper> pluginWrappers = new ArrayList<>(startedPlugins.size());
        for (PluginWrapper value : startedPlugins.values()) {
            pluginWrappers.add(new PluginWrapperFace(value));
        }
        return pluginWrappers;
    }

    @Override
    public PluginWrapper getPluginWrapper(String pluginId) {
        return startedPlugins.get(pluginId);
    }

    protected PluginDescriptor start(PluginDescriptor pluginDescriptor) throws Exception {
        String pluginId = pluginDescriptor.getPluginId();
        if(startedPlugins.containsKey(pluginId)){
            throw new PluginException("已经存在插件: " + MsgUtils.getPluginUnique(pluginDescriptor));
        }
        provider.getPluginChecker().check(pluginDescriptor);
        PluginWrapperInside pluginWrapper = new PluginWrapperInside(pluginDescriptor);
        pluginWrapper.setPluginState(PluginState.RESOLVED);
        start(pluginWrapper);
        return pluginDescriptor;
    }

    protected void start(PluginWrapperInside pluginWrapper) throws Exception{
        String pluginId = pluginWrapper.getPluginId();
        pluginWrapper.setPluginState(PluginState.STARTED);
        startedPlugins.put(pluginId, pluginWrapper);
        resolvedPlugins.remove(pluginId);
    }

    protected void stop(PluginWrapperInside pluginWrapper) throws Exception{
        String pluginId = pluginWrapper.getPluginId();
        pluginWrapper.setPluginState(PluginState.STOPPED);
        resolvedPlugins.put(pluginId, pluginWrapper.getPluginDescriptor());
        startedPlugins.remove(pluginId);
    }



}
