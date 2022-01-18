package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.scanner.ComposePathResolve;
import com.gitee.starblues.core.scanner.DevPathResolve;
import com.gitee.starblues.core.scanner.PathResolve;
import com.gitee.starblues.core.scanner.ProdPathResolve;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.utils.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象的插件管理者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginManager implements PluginManager{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RealizeProvider provider;
    private final List<String> pluginRootDirs;

    private final PathResolve pathResolve;

    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private final Map<String, PluginInsideInfo> startedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginInsideInfo> resolvedPlugins = new ConcurrentHashMap<>();

    private Set<String> disabledPluginIds;
    private List<String> sortedPluginIds;


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
        this.pathResolve = new ComposePathResolve(new DevPathResolve(), new ProdPathResolve());
    }

    public void setDisabledPluginIds(Set<String> disabledPluginIds) {
        this.disabledPluginIds = disabledPluginIds;
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
                StringBuilder warn = new StringBuilder("\n\n路径 {} 中未发现插件.\n");
                warn.append("请检查路径是否合适.\n");
                if(provider.getRuntimeMode() == RuntimeMode.DEV){
                    warn.append("请检查插件包是否编译.\n");
                }
                log.warn(warn.toString(), pluginRootDirs);
                return Collections.emptyList();
            }
            Map<String, PluginInfo> pluginInfoMap = new LinkedHashMap<>(scanPluginPaths.size());
            for (Path path : scanPluginPaths) {
                try {
                    PluginInsideInfo pluginInfo = loadPlugin(path, false);
                    if(pluginInfo != null){
                        pluginInfoMap.put(pluginInfo.getPluginId(), pluginInfo.toPluginInfo());
                    }
                } catch (PluginException e) {
                    log.error("加载插件包失败: {}. {}", path, e.getMessage(), e);
                }
            }
            return getSortPlugin(pluginInfoMap);
        } finally {
            loaded.set(true);
        }
    }

    @Override
    public boolean verify(Path pluginPath) {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            provider.getPluginChecker().check(pluginPath);
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            if(pluginDescriptor == null){
                return false;
            }
            provider.getPluginChecker().check(pluginDescriptor);
            return true;
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
            // 拷贝插件
            pluginPath = copyPlugin(pluginPath, unpackPlugin);
            // 加载插件
            PluginInsideInfo pluginInsideInfo = loadPlugin(pluginPath, true);
            if(pluginInsideInfo != null){
                return pluginInsideInfo.toPluginInfo();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw PluginException.getPluginException(e, ()-> {
                throw new PluginException("插件包加载失败: " + sourcePluginPath, e);
            });
        }
    }

    @Override
    public synchronized void unLoad(String pluginId) {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        resolvedPlugins.remove(pluginId);
    }

    @Override
    public synchronized PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        PluginInfo loadPluginInfo = load(pluginPath, unpackPlugin);
        if(loadPluginInfo == null){
            throw new PluginException("插件包安装失败: " + pluginPath);
        }
        try {
            PluginInsideInfo pluginInsideInfo = resolvedPlugins.get(loadPluginInfo.getPluginId());
            start(pluginInsideInfo);
            log.info("安装插件[{}]成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            return pluginInsideInfo.toPluginInfo();
        } catch (Exception e){
            if(e instanceof PluginDisabledException){
                throw (PluginDisabledException)e;
            }
            throw PluginException.getPluginException(e, ()-> {
                unLoad(loadPluginInfo.getPluginId());
                throw new PluginException("插件包安装失败: " + pluginPath);
            });
        }
    }

    @Override
    public synchronized void uninstall(String pluginId) throws PluginException {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        PluginInsideInfo wrapperInside = getPlugin(pluginId);
        if(wrapperInside == null){
            throw new PluginException("没有发现插件: " + pluginId);
        }
        if(wrapperInside.getPluginState() == PluginState.STARTED){
            try {
                stop(wrapperInside);
            } catch (Exception e) {
                throw PluginException.getPluginException(e, ()-> new PluginException("停止", pluginId, e));
            }
        }
        startedPlugins.remove(pluginId);
        resolvedPlugins.remove(pluginId);
        MsgUtils.info(log, wrapperInside.getPluginDescriptor(), "卸载成功");
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
        checkDisabled(upgradePlugin, "更新");
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
        try {
            start(pluginInsideInfo);
            log.info("启动插件[{}]成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            return pluginInsideInfo.toPluginInfo();
        } catch (Exception e){
            throw PluginException.getPluginException(e, ()-> new PluginException(pluginInsideInfo.getPluginDescriptor(), "启动失败", e));
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
        try {
            stop(pluginInsideInfo);
            log.info("停止插件[{}]成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            return pluginInsideInfo.toPluginInfo();
        } catch (Exception e) {
            throw PluginException.getPluginException(e, ()-> new PluginException(pluginInsideInfo.getPluginDescriptor(), "停止失败", e));
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
            throw new PluginException("路径中未发现合法插件");
        }
        String pluginId = pluginInsideInfo.getPluginId();
        if(resolvedPlugins.containsKey(pluginId)){
            throw new PluginException(pluginInsideInfo.getPluginDescriptor(), "已经被加载");
        }
        resolvedPlugins.put(pluginId, pluginInsideInfo);
        MsgUtils.info(log, pluginInsideInfo.getPluginDescriptor(), "加载成功");
        return pluginInsideInfo;
    }

    protected PluginInsideInfo loadFromPath(Path pluginPath) {
        try {
            provider.getPluginChecker().check(pluginPath);
        } catch (Exception e) {
            throw PluginException.getPluginException(e, ()-> {
                return new PluginException("非法插件包. " + e.getMessage(), e);
            });
        }

        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()){
            InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            if(pluginDescriptor == null){
                throw new PluginException("非法插件包: " + pluginPath);
            }
            String pluginId = pluginDescriptor.getPluginId();
            PluginInsideInfo pluginInsideInfo = new DefaultPluginInsideInfo(pluginDescriptor);
            if(isDisabled(pluginId)){
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
        if(isDev()){
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
                String unpackPluginPath = CommonUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName);
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
            targetFile = Paths.get(CommonUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName)).toFile();
            if(pluginFile.isFile()){
                if(unpackPlugin){
                    // 需要解压
                    String unpackPluginPath = CommonUtils.joiningFilePath(pluginRootDir.getPath(), resolvePluginFileName);
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
        InsidePluginDescriptor pluginDescriptor = pluginInsideInfo.getPluginDescriptor();
        checkCanStarted(pluginInsideInfo);
        provider.getPluginChecker().check(pluginDescriptor);
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
        checkCanStopped(pluginInsideInfo);
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
     * 是否是开发环境
     * @return boolean
     */
    private boolean isDev(){
        return provider.getRuntimeMode() == RuntimeMode.DEV;
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

    /**
     * 检查插件是否可启动
     * @param pluginInsideInfo PluginInfo
     */
    private void checkCanStarted(PluginInfo pluginInsideInfo){
        checkDisabled(pluginInsideInfo, "启动");
        PluginState pluginState = pluginInsideInfo.getPluginState();
        if(pluginState == PluginState.STARTED){
            throw new PluginException(pluginInsideInfo.getPluginDescriptor(), "已经启动, 不能再启动");
        }
    }

    /**
     * 检查插件是否能停止
     * @param pluginInsideInfo PluginInfo
     */
    private void checkCanStopped(PluginInfo pluginInsideInfo){
        checkDisabled(pluginInsideInfo, "停止");
        PluginState pluginState = pluginInsideInfo.getPluginState();
        if(pluginState != PluginState.STARTED){
            throw new PluginException(pluginInsideInfo.getPluginDescriptor(), "没有启动, 不能停止");
        }
    }

    /**
     * 检查插件是否被禁用
     * @param pluginInsideInfo PluginInfo
     */
    private void checkDisabled(PluginInfo pluginInsideInfo, String opType){
        if(pluginInsideInfo.getPluginState() == PluginState.DISABLED || isDisabled(pluginInsideInfo.getPluginId())){
            throw new PluginDisabledException(pluginInsideInfo.getPluginDescriptor(), opType);
        }
    }

    /**
     * 插件是否被禁用
     * @param pluginId 插件id
     * @return  boolean
     */
    private boolean isDisabled(String pluginId){
        return disabledPluginIds != null && disabledPluginIds.contains(pluginId);
    }

}
