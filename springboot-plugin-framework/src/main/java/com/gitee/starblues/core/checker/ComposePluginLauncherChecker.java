package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合插件检查者
 * @author starBlues
 * @version 3.0.0
 */
public class ComposePluginLauncherChecker implements PluginLauncherChecker {

    private final List<PluginLauncherChecker> pluginCheckers;

    public ComposePluginLauncherChecker() {
        this(new ArrayList<>());
    }

    public ComposePluginLauncherChecker(List<PluginLauncherChecker> pluginCheckers) {
        this.pluginCheckers = pluginCheckers;
    }

    public void add(PluginLauncherChecker pluginChecker){
        if(pluginChecker != null){
            this.pluginCheckers.add(pluginChecker);
        }
    }


    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
        for (PluginLauncherChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkCanStart(pluginInfo);
        }
    }

    @Override
    public void checkCanStop(PluginInfo pluginInfo) throws PluginException {
        for (PluginLauncherChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkCanStop(pluginInfo);
        }
    }

}
