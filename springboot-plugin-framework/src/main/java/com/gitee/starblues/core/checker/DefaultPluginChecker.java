package com.gitee.starblues.core.checker;

import com.gitee.starblues.common.Constants;
import com.gitee.starblues.common.DependencyPlugin;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.core.PluginChecker;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginChecker implements PluginChecker {


    protected final RealizeProvider realizeProvider;
    protected final IntegrationConfiguration configuration;

    private final Set<String> enablePluginIds;
    private final Set<String> disabledPluginIds;

    public DefaultPluginChecker(RealizeProvider realizeProvider,
                                IntegrationConfiguration configuration) {
        this.realizeProvider = realizeProvider;
        this.configuration = configuration;
        this.enablePluginIds = configuration.enablePluginIds();
        this.disabledPluginIds = configuration.disablePluginIds();
    }

    @Override
    public void check(Path path) throws Exception {
        if(path == null){
            throw new FileNotFoundException("path 文件路径不能为空");
        }
        if(Files.notExists(path)){
            throw new FileNotFoundException("不存在文件: " + path.toString());
        }
    }

    @Override
    public void checkDescriptor(PluginDescriptor descriptor) throws PluginException {
        Assert.isNotNull(descriptor, "PluginDescriptor 不能为空");

        Assert.isNotEmpty(descriptor.getPluginPath(), "pluginPath 不能为空");

        Assert.isNotNull(descriptor.getPluginId(),
                PluginDescriptorKey.PLUGIN_ID + "不能为空");

        Assert.isNotNull(descriptor.getPluginBootstrapClass(),
                PluginDescriptorKey.PLUGIN_BOOTSTRAP_CLASS + "不能为空");

        Assert.isNotNull(descriptor.getPluginVersion(),
                PluginDescriptorKey.PLUGIN_VERSION + "不能为空");

        String illegal = PackageStructure.getIllegal(descriptor.getPluginId());
        if(illegal != null){
            throw new PluginException(descriptor, "插件id不能包含:" + illegal);
        }
        illegal = PackageStructure.getIllegal(descriptor.getPluginVersion());
        if(illegal != null){
            throw new PluginException(descriptor, "插件版本号不能包含:" + illegal);
        }
    }


    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
        checkDescriptor(pluginInfo.getPluginDescriptor());
        PluginDisabledException.checkDisabled(pluginInfo, configuration, "启动");
        PluginState pluginState = pluginInfo.getPluginState();
        if(pluginState == PluginState.STARTED){
            throw new PluginException(pluginInfo.getPluginDescriptor(), "已经启动, 不能再启动");
        }
        checkRequiresVersion(pluginInfo);
    }


    @Override
    public void checkCanStop(PluginInfo pluginInfo) throws PluginException {
        PluginDisabledException.checkDisabled(pluginInfo, configuration, "停止");
        PluginState pluginState = pluginInfo.getPluginState();
        if(pluginState != PluginState.STARTED){
            throw new PluginException(pluginInfo.getPluginDescriptor(), "没有启动, 不能停止");
        }
    }

    /**
     * 检查可安装到主程序的版本
     * @param pluginInfo pluginInfo
     */
    private void checkRequiresVersion(PluginInfo pluginInfo){
        String version = configuration.version();
        if(ObjectUtils.isEmpty(version) || Constants.ALLOW_VERSION.equals(version)){
            return;
        }
        String requires = pluginInfo.getPluginDescriptor().getRequires();
        boolean exactVersion = configuration.exactVersion();
        int compareVersion = realizeProvider.getVersionInspector().compareTo(requires, version);
        if(exactVersion && compareVersion != 0){
            String error = "需要安装到[" + requires + "]版本的主程序, 但当前主程序版本为[" + version + "]";
            throw new PluginException(pluginInfo.getPluginDescriptor(), error);
        }
        if(compareVersion > 0){
            String error = "需要安装到小于等于[" + requires + "]版本的主程序, 但当前主程序版本为[" + version + "]";
            throw new PluginException(pluginInfo.getPluginDescriptor(), error);
        }
    }





}
