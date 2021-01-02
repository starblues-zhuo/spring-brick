package com.gitee.starblues.integration.pf4j;

import org.pf4j.PluginStatusProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * @author starBlues
 * @version 2.3
 */
public class ConfigPluginStatusProvider implements PluginStatusProvider {

    private Set<String> disabledPlugins = new HashSet<>();

    public ConfigPluginStatusProvider() {
        this(null);
    }

    public ConfigPluginStatusProvider(Set<String> disabledPluginIds) {
        if(disabledPluginIds != null && !disabledPluginIds.isEmpty()){
            this.disabledPlugins.addAll(disabledPluginIds);
        }
    }


    @Override
    public boolean isPluginDisabled(String pluginId) {
        if(disabledPlugins.contains("*")){
            return true;
        }
        if(pluginId == null || "".equals(pluginId)){
            return true;
        }
        return disabledPlugins.contains(pluginId);
    }

    @Override
    public void disablePlugin(String pluginId) {
        if (isPluginDisabled(pluginId)) {
            return;
        }
        disabledPlugins.add(pluginId);
    }

    @Override
    public void enablePlugin(String pluginId) {
        if (!isPluginDisabled(pluginId)) {
            return;
        }
        disabledPlugins.remove(pluginId);
    }
}
