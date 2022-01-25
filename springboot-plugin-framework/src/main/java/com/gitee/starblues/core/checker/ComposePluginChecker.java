package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.PluginChecker;
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
public class ComposePluginChecker implements PluginChecker {

    private final List<PluginChecker> pluginCheckers;

    public ComposePluginChecker() {
        this(new ArrayList<>());
    }

    public ComposePluginChecker(List<PluginChecker> pluginCheckers) {
        this.pluginCheckers = pluginCheckers;
    }

    public void add(PluginChecker pluginChecker){
        if(pluginChecker != null){
            this.pluginCheckers.add(pluginChecker);
        }
    }


    @Override
    public void check(Path path) throws Exception {
        for (PluginChecker pluginChecker : pluginCheckers) {
            pluginChecker.check(path);
        }
    }

    @Override
    public void checkDescriptor(PluginDescriptor descriptor) throws PluginException {
        for (PluginChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkDescriptor(descriptor);
        }
    }

    @Override
    public void checkCanStart(PluginInfo pluginInfo) throws PluginException {
        for (PluginChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkCanStart(pluginInfo);
        }
    }

    @Override
    public void checkCanStop(PluginInfo pluginInfo) throws PluginException {
        for (PluginChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkCanStart(pluginInfo);
        }
    }

}
