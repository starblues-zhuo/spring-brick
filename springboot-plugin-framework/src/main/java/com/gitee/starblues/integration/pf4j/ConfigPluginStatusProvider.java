package com.gitee.starblues.integration.pf4j;

import org.pf4j.PluginStatusProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * @author starBlues
 * @version 2.4.0
 */
@Deprecated
public class ConfigPluginStatusProvider implements PluginStatusProvider {

    private Set<String> enablePluginIds = new HashSet<>();
    private Set<String> disabledPlugins = new HashSet<>();

    public ConfigPluginStatusProvider() {
        this(null, null);
    }

    public ConfigPluginStatusProvider(Set<String> enablePluginIds,
                                      Set<String> disabledPluginIds) {
        if(enablePluginIds != null && !enablePluginIds.isEmpty()){
            this.enablePluginIds.addAll(enablePluginIds);
        }
        if(disabledPluginIds != null && !disabledPluginIds.isEmpty()){
            this.disabledPlugins.addAll(disabledPluginIds);
        }
    }


    @Override
    public boolean isPluginDisabled(String pluginId) {
        if(disabledPlugins.contains("*")){
            return true;
        }
        if (disabledPlugins.contains(pluginId)) {
            return true;
        }

        return !enablePluginIds.isEmpty() && !enablePluginIds.contains(pluginId);
    }

    @Override
    public void disablePlugin(String pluginId) {
        if (isPluginDisabled(pluginId)) {
            return;
        }
        disabledPlugins.add(pluginId);
        enablePluginIds.remove(pluginId);
    }

    @Override
    public void enablePlugin(String pluginId) {
        if (!isPluginDisabled(pluginId)) {
            return;
        }
        disabledPlugins.remove(pluginId);
        enablePluginIds.add(pluginId);
    }
}
