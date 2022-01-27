package com.gitee.starblues.core.checker;

import com.gitee.starblues.common.Constants;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginLauncherChecker implements PluginLauncherChecker {


    protected final RealizeProvider realizeProvider;
    protected final IntegrationConfiguration configuration;


    public DefaultPluginLauncherChecker(RealizeProvider realizeProvider,
                                        IntegrationConfiguration configuration) {
        this.realizeProvider = realizeProvider;
        this.configuration = configuration;
    }


    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
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
