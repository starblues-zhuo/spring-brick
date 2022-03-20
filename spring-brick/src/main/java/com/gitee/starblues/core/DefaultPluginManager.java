/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core;

import com.gitee.starblues.core.checker.ComposePluginLauncherChecker;
import com.gitee.starblues.core.checker.DefaultPluginLauncherChecker;
import com.gitee.starblues.core.checker.DependencyPluginLauncherChecker;
import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.scanner.ComposePathResolve;
import com.gitee.starblues.core.scanner.DevPathResolve;
import com.gitee.starblues.core.scanner.PathResolve;
import com.gitee.starblues.core.scanner.ProdPathResolve;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.DefaultPluginListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.utils.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
    private final IntegrationConfiguration configuration;
    private final List<String> pluginRootDirs;

    private final PathResolve pathResolve;
    private final PluginBasicChecker basicChecker;

    protected final ComposePluginLauncherChecker launcherChecker;

    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private final Map<String, PluginInsideInfo> startedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginInsideInfo> resolvedPlugins = new ConcurrentHashMap<>();

    protected PluginListenerFactory pluginListenerFactory;


    private List<String> sortedPluginIds;

    public DefaultPluginManager(RealizeProvider realizeProvider, IntegrationConfiguration configuration) {
        this.provider = Assert.isNotNull(realizeProvider, "参数 realizeProvider 不能为空");
        this.configuration = Assert.isNotNull(configuration, "参数 configuration 不能为空");
        this.pluginRootDirs = resolvePath(configuration.pluginPath());
        this.pathResolve = getComposePathResolve();
        this.basicChecker = realizeProvider.getPluginBasicChecker();
        this.launcherChecker = getComposeLauncherChecker(realizeProvider);
        setSortedPluginIds(configuration.sortInitPluginIds());
    }

    protected ComposePluginLauncherChecker getComposeLauncherChecker(RealizeProvider realizeProvider){
        ComposePluginLauncherChecker checker = new ComposePluginLauncherChecker();
        checker.add(new DefaultPluginLauncherChecker(realizeProvider, configuration));
        checker.add(new DependencyPluginLauncherChecker(this));
        return checker;
    }

    protected ComposePathResolve getComposePathResolve(){
        return new ComposePathResolve(new DevPathResolve(), new ProdPathResolve());
    }

    public void setSortedPluginIds(List<String> sortedPluginIds) {
        this.sortedPluginIds = sortedPluginIds;
    }

    @Override
    public List<String> getPluginsRoots() {
        return new ArrayList<>(pluginRootDirs);
    }

    @Override
    public String getDefaultPluginRoot() {
        if(pluginRootDirs == null){
            return null;
        }
        return pluginRootDirs.stream().findFirst().orElseThrow(()->{
            return new PluginException("插件根路径未配置");
        });
    }

    @Override
    public synchronized List<PluginInfo> loadPlugins() {
        if(loaded.get()){
            throw new RuntimeException("已经加载过了插件, 不能在重复调用: loadPlugins");
        }
        try {
            if(ObjectUtils.isEmpty(pluginRootDirs)){
                log.warn("插件根目录为空, 无法加载插件.");
                return Collections.emptyList();
            }
            List<Path> scanPluginPaths = provider.getPluginScanner().scan(pluginRootDirs);
            if(ObjectUtils.isEmpty(scanPluginPaths)){
                StringBuilder warn = new StringBuilder("以下路径未发现插件: \n");
                for (int i = 0; i < pluginRootDirs.size(); i++) {
                    warn.append(i + 1).append(". ").append(pluginRootDirs.get(i)).append("\n");
                }
                warn.append("请检查路径是否合适.\n");
                warn.append("请检查配置[plugin.runMode]是否合适.\n");
                if(provider.getRuntimeMode() == RuntimeMode.DEV){
                    warn.append("请检查插件包是否编译.\n");
                } else {
                    warn.append("请检查插件是否合法.\n");
                }
                log.warn(warn.toString());
                return Collections.emptyList();
            }
            pluginListenerFactory = createPluginListenerFactory();
            Map<String, PluginInfo> pluginInfoMap = new LinkedHashMap<>(scanPluginPaths.size());
            for (Path path : scanPluginPaths) {
                try {
                    PluginInsideInfo pluginInfo = loadPlugin(path, false);
                    if(pluginInfo != null){
                        pluginInfo.setFollowSystem();
                        PluginInfo pluginInfoFace = pluginInfo.toPluginInfo();
                        pluginListenerFactory.loadSuccess(pluginInfoFace);
                        pluginInfoMap.put(pluginInfo.getPluginId(), pluginInfoFace);
                    }
                } catch (PluginException e) {
                    pluginListenerFactory.loadFailure(path, e);
                    log.error("加载插件包失败: {}. {}", path, e.getMessage(), e);
                }
            }
            return getSortPlugin(pluginInfoMap);
        } finally {
            loaded.set(true);
        }
    }

    protected PluginListenerFactory createPluginListenerFactory(){
        return new DefaultPluginListenerFactory();
    }

    @Override
    public boolean verify(Path pluginPath) {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            basicChecker.checkPath(pluginPath);
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            return pluginDescriptor != null;
        } catch (Exception e) {
            log.error("插件jar包校验失败: {}" , pluginPath, e);
            return false;
        }
    }

    @Override
    public PluginInfo parse(Path pluginPath) throws PluginException {
        PluginInsideInfo pluginInsideInfo = loadFromPath(pluginPath);
        if(pluginInsideInfo == null){
            throw new PluginException("非法插件包: " + pluginPath);
        }
        return pluginInsideInfo.toPluginInfo();
    }

    @Override
    public synchronized PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        String sourcePluginPath = pluginPath.toString();
        try {
            // 解析插件
            PluginInfo pluginInfo = parse(pluginPath);
            // 检查是否存在当前插件
            PluginInsideInfo plugin = getPlugin(pluginInfo.getPluginId());
            if(plugin != null){
                // 已经存在该插件
                throw new PluginException("加载插件包[" + pluginPath + "]失败. 已经存在该插件: " +
                        MsgUtils.getPluginUnique(plugin.getPluginDescriptor()));
            }
            if(configuration.isProd()){
                // 如果为生产环境, 则拷贝插件
                pluginPath = copyPlugin(pluginPath, unpackPlugin);
            }
            // 加载插件
            PluginInsideInfo pluginInsideInfo = loadPlugin(pluginPath, true);
            if(pluginInsideInfo != null){
                PluginInfo pluginInfoFace = pluginInsideInfo.toPluginInfo();
                pluginListenerFactory.loadSuccess(pluginInfoFace);
                return pluginInfoFace;
            } else {
                pluginListenerFactory.loadFailure(pluginPath, new PluginException("Not found PluginInsideInfo"));
                return null;
            }
        } catch (Exception e) {
            PluginException pluginException = PluginException.getPluginException(e, () -> {
                throw new PluginException("插件包加载失败: " + sourcePluginPath, e);
            });
            pluginListenerFactory.loadFailure(pluginPath, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized void unLoad(String pluginId) {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        PluginInsideInfo pluginInsideInfo = resolvedPlugins.remove(pluginId);
        pluginListenerFactory.unLoadSuccess(pluginInsideInfo.toPluginInfo());
    }

    @Override
    public synchronized PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        PluginInfo loadPluginInfo = load(pluginPath, unpackPlugin);
        if(loadPluginInfo == null){
            throw new PluginException("插件包安装失败: " + pluginPath);
        }
        PluginInsideInfo pluginInsideInfo = resolvedPlugins.get(loadPluginInfo.getPluginId());
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            start(pluginInsideInfo);
            pluginListenerFactory.startSuccess(pluginInfo);
            log.info("插件[{}]安装成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            return pluginInsideInfo.toPluginInfo();
        } catch (Exception e){
            if(e instanceof PluginDisabledException){
                throw (PluginDisabledException)e;
            }
            PluginException pluginException = PluginException.getPluginException(e, ()-> {
                unLoad(loadPluginInfo.getPluginId());
                throw new PluginException("插件包[ " + pluginPath + " ]安装失败: " + e.getMessage(), e);
            });
            pluginListenerFactory.startFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized void uninstall(String pluginId) throws PluginException {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        PluginInsideInfo wrapperInside = getPlugin(pluginId);
        if(wrapperInside == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = wrapperInside.toPluginInfo();
        if(wrapperInside.getPluginState() == PluginState.STARTED){
            try {
                stop(wrapperInside);
                pluginListenerFactory.stopSuccess(pluginInfo);
            } catch (Exception e) {
                PluginException pluginException = PluginException.getPluginException(e,
                        ()-> new PluginException("停止", pluginId, e));
                pluginListenerFactory.stopFailure(pluginInfo, pluginException);
                throw pluginException;
            }
        }
        startedPlugins.remove(pluginId);
        unLoad(pluginId);
        LogUtils.info(log, wrapperInside.getPluginDescriptor(), "卸载成功");
    }

    @Override
    public synchronized PluginInfo upgrade(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        // 解析插件包
        PluginInfo upgradePlugin = parse(pluginPath);
        if(upgradePlugin == null){
            throw new PluginException("非法插件包: " + pluginPath);
        }
        // 检查插件是否被禁用
        PluginDisabledException.checkDisabled(upgradePlugin, configuration, "更新");
        String pluginId = upgradePlugin.getPluginId();
        // 得到旧插件
        PluginInsideInfo oldPlugin = getPlugin(pluginId);
        if(oldPlugin == null){
            // 旧插件为空, 则直接安装新插件
            return install(pluginPath, unpackPlugin);
        }
        // 检查插件版本
        PluginDescriptor upgradePluginDescriptor = upgradePlugin.getPluginDescriptor();
        checkVersion(oldPlugin.getPluginDescriptor().getPluginVersion(), upgradePluginDescriptor.getPluginVersion());
        if(oldPlugin.getPluginState() == PluginState.STARTED){
            // 如果插件被启动, 则卸载旧的插件
            uninstall(pluginId);
        } else if(oldPlugin.getPluginState() == PluginState.LOADED){
            // 如果插件被load
            unLoad(pluginId);
        }
        try {
            // 安装新插件
            install(pluginPath, unpackPlugin);
            log.info("更新插件[{}]成功", MsgUtils.getPluginUnique(upgradePluginDescriptor));
            return upgradePlugin;
        } catch (Exception e){
            throw PluginException.getPluginException(e, ()-> new PluginException(upgradePluginDescriptor, "更新失败", e));
        }
    }

    @Override
    public synchronized PluginInfo start(String pluginId) throws PluginException {
        if(ObjectUtils.isEmpty(pluginId)){
            return null;
        }
        PluginInsideInfo pluginInsideInfo = getPlugin(pluginId);
        if(pluginInsideInfo == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            start(pluginInsideInfo);
            log.info("插件[{}]启动成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            pluginListenerFactory.startSuccess(pluginInfo);
            return pluginInfo;
        } catch (Exception e){
            PluginException pluginException = PluginException.getPluginException(e,
                    ()-> new PluginException(pluginInsideInfo.getPluginDescriptor(), "启动失败", e));
            pluginListenerFactory.startFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized PluginInfo stop(String pluginId) throws PluginException {
        if(ObjectUtils.isEmpty(pluginId)){
            return null;
        }
        PluginInsideInfo pluginInsideInfo = startedPlugins.get(pluginId);
        if(pluginInsideInfo == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            stop(pluginInsideInfo);
            log.info("停止插件[{}]成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            pluginListenerFactory.stopSuccess(pluginInfo);
            return pluginInfo;
        } catch (Exception e) {
            PluginException pluginException = PluginException.getPluginException(e,
                    () -> new PluginException(pluginInsideInfo.getPluginDescriptor(), "停止失败", e));
            pluginListenerFactory.stopFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized PluginInfo getPluginInfo(String pluginId) {
        if(ObjectUtils.isEmpty(pluginId)){
            return null;
        }
        PluginInsideInfo wrapperInside = startedPlugins.get(pluginId);
        if(wrapperInside == null){
            wrapperInside = resolvedPlugins.get(pluginId);
        }
        if(wrapperInside != null){
            return wrapperInside.toPluginInfo();
        } else {
            return null;
        }
    }

    @Override
    public synchronized List<PluginInfo> getPluginInfos() {
        List<PluginInfo> pluginDescriptors = new ArrayList<>(
                resolvedPlugins.size() + startedPlugins.size());
        for (PluginInsideInfo wrapperInside : startedPlugins.values()) {
            pluginDescriptors.add(wrapperInside.toPluginInfo());
        }
        for (PluginInsideInfo wrapperInside : resolvedPlugins.values()) {
            pluginDescriptors.add(wrapperInside.toPluginInfo());
        }
        return pluginDescriptors;
    }

    protected PluginInsideInfo loadPlugin(Path pluginPath, boolean resolvePath) {
        if(resolvePath){
            Path sourcePluginPath = pluginPath;
            pluginPath = pathResolve.resolve(pluginPath);
            if(pluginPath == null){
                throw new PluginException("未发现插件: " + sourcePluginPath);
            }
        }
        PluginInsideInfo pluginInsideInfo = loadFromPath(pluginPath);
        if(pluginInsideInfo == null){
            return null;
        }
        String pluginId = pluginInsideInfo.getPluginId();
        if(resolvedPlugins.containsKey(pluginId)){
            throw new PluginException(pluginInsideInfo.getPluginDescriptor(), "已经被加载");
        }
        resolvedPlugins.put(pluginId, pluginInsideInfo);
        LogUtils.info(log, pluginInsideInfo.getPluginDescriptor(), "加载成功");
        return pluginInsideInfo;
    }

    protected PluginInsideInfo loadFromPath(Path pluginPath) {
        try {
            basicChecker.checkPath(pluginPath);
        } catch (Exception e) {
            throw PluginException.getPluginException(e, ()-> {
                return new PluginException("非法插件包. " + e.getMessage(), e);
            });
        }

        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            if(pluginDescriptor == null){
                return null;
            }
            String pluginId = pluginDescriptor.getPluginId();
            PluginInsideInfo pluginInsideInfo = new DefaultPluginInsideInfo(pluginDescriptor);
            if(configuration.isDisabled(pluginId)){
                pluginInsideInfo.setPluginState(PluginState.DISABLED);
            } else {
                pluginInsideInfo.setPluginState(PluginState.LOADED);
            }
            return pluginInsideInfo;
        } catch (Exception e){
            throw PluginException.getPluginException(e, ()-> new PluginException("加载插件失败"));
        }
    }

    /**
     * 拷贝插件文件到插件根目录
     * @param pluginPath 源插件文件路径
     * @param unpackPlugin 是否解压插件包. 只有为压缩类型包才可解压
     * @return 拷贝后的插件路径
     * @throws IOException IO 异常
     */
    protected Path copyPlugin(Path pluginPath, boolean unpackPlugin) throws IOException {
        if(configuration.isDev()){
            return pluginPath;
        }
        File targetFile = pluginPath.toFile();
        if(!targetFile.exists()) {
            throw new PluginException("不存在插件文件: " + pluginPath);
        }
        String targetFileName = targetFile.getName();
        // 先判断当前插件文件是否在插件目录中
        File pluginRootDir = null;
        for (String dir : pluginRootDirs) {
            File rootDir = new File(dir);
            if(targetFile.getParentFile().compareTo(rootDir) == 0){
                pluginRootDir = rootDir;
                break;
            }
        }
        String resolvePluginFileName = unpackPlugin ? PluginFileUtils.getFileName(targetFile) : targetFileName;
        Path resultPath = null;
        if(pluginRootDir != null){
            // 在根目录中存在
            if(targetFile.isFile() && unpackPlugin){
                // 需要解压, 检查解压后的文件名称是否存在同名文件
                checkExistFile(pluginRootDir, resolvePluginFileName);
                String unpackPluginPath = FilesUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName);
                PluginFileUtils.decompressZip(targetFile.getPath(), unpackPluginPath);
                resultPath = Paths.get(unpackPluginPath);
                PluginFileUtils.deleteFile(targetFile);
            } else {
                resultPath = targetFile.toPath();
            }
        } else {
            File pluginFile = pluginPath.toFile();
            pluginRootDir = new File(getDefaultPluginRoot());
            File pluginRootDirFile = new File(getDefaultPluginRoot());
            // 检查是否存在同名文件
            checkExistFile(pluginRootDirFile, resolvePluginFileName);
            targetFile = Paths.get(FilesUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName)).toFile();
            if(pluginFile.isFile()){
                if(unpackPlugin){
                    // 需要解压
                    String unpackPluginPath = FilesUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName);
                    PluginFileUtils.decompressZip(pluginFile.getPath(), unpackPluginPath);
                    resultPath = Paths.get(unpackPluginPath);
                } else {
                    FileUtils.copyFile(pluginFile, targetFile);
                    resultPath = targetFile.toPath();
                }
            } else {
                FileUtils.copyDirectory(pluginFile, targetFile);
                resultPath = targetFile.toPath();
            }
        }
        return resultPath;
    }


    /**
     * 统一启动插件操作
     * @param pluginInsideInfo PluginInsideInfo
     * @throws Exception 启动异常
     */
    protected void start(PluginInsideInfo pluginInsideInfo) throws Exception{
        Assert.isNotNull(pluginInsideInfo, "pluginInsideInfo 参数不能为空");
        String pluginId = pluginInsideInfo.getPluginId();
        launcherChecker.checkCanStart(pluginInsideInfo);
        pluginInsideInfo.setPluginState(PluginState.STARTED);
        startedPlugins.put(pluginId, pluginInsideInfo);
        resolvedPlugins.remove(pluginId);
    }

    /**
     * 统一停止插件操作
     * @param pluginInsideInfo PluginInsideInfo
     * @throws Exception 启动异常
     */
    protected void stop(PluginInsideInfo pluginInsideInfo) throws Exception{
        launcherChecker.checkCanStop(pluginInsideInfo);
        String pluginId = pluginInsideInfo.getPluginId();
        pluginInsideInfo.setPluginState(PluginState.STOPPED);
        resolvedPlugins.put(pluginId, pluginInsideInfo);
        startedPlugins.remove(pluginId);
    }

    /**
     * 根据配置重新排序插件
     * @param pluginInfos 未排序的查询信息
     * @return 排序的插件信息
     */
    protected List<PluginInfo> getSortPlugin(Map<String, PluginInfo> pluginInfos){
        if (ObjectUtils.isEmpty(sortedPluginIds)) {
            return new ArrayList<>(pluginInfos.values());
        }
        List<PluginInfo> sortPluginInfos = new ArrayList<>();
        for (String sortedPluginId : sortedPluginIds) {
            PluginInfo pluginInfo = pluginInfos.get(sortedPluginId);
            if(pluginInfo != null){
                sortPluginInfos.add(pluginInfo);
                pluginInfos.remove(sortedPluginId);
            }
        }
        sortPluginInfos.addAll(pluginInfos.values());
        return sortPluginInfos;
    }


    protected PluginInsideInfo getPlugin(String pluginId){
        PluginInsideInfo wrapperInside = startedPlugins.get(pluginId);
        if(wrapperInside == null){
            wrapperInside = resolvedPlugins.get(pluginId);
        }
        return wrapperInside;
    }

    /**
     * 检查是否目录中是否存在同名插件
     * @param dirFile 目录文件
     * @param pluginFileName  检查的插件名
     */
    private void checkExistFile(File dirFile, String pluginFileName) {
        if(ResourceUtils.existFile(dirFile, pluginFileName)){
            // 插件根目录存在同名插件文件
            throw getExistFileException(dirFile.getPath(), pluginFileName);
        }
    }

    private PluginException getExistFileException(String rootPath, String pluginFileName){
        return new PluginException("插件目录[" + rootPath + "]存在同名文件: " + pluginFileName);
    }

    /**
     * 检查比较插件版本
     * @param oldPluginVersion 旧插件版本
     * @param newPluginVersion 新插件版本
     */
    protected void checkVersion(String oldPluginVersion, String newPluginVersion){
        int compareVersion = provider.getVersionInspector().compareTo(oldPluginVersion, newPluginVersion);
        if(compareVersion <= 0){
            throw new PluginException("插件包版本[" + newPluginVersion + "]必须大于:"
                    + oldPluginVersion);
        }
    }

    private List<String> resolvePath(List<String> path){
        if(ObjectUtils.isEmpty(path)){
            return Collections.emptyList();
        } else {
            File file = new File("");
            String absolutePath = file.getAbsolutePath();
            return path.stream()
                    .filter(p->!ObjectUtils.isEmpty(p))
                    .map(p->FilesUtils.resolveRelativePath(absolutePath, p))
                    .collect(Collectors.toList());
        }
    }


}
