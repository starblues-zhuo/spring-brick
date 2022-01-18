package com.gitee.starblues.core;

import java.util.List;
import java.util.Set;

/**
 * 配置
 * @author starBlues
 * @version 3.0.0
 */
public class PluginConfiguration {

    private Set<String> disabledPluginIds;
    private List<String> sortedPluginIds;

    public void setDisabledPluginIds(Set<String> disabledPluginIds) {
        this.disabledPluginIds = disabledPluginIds;
    }

    public void setSortedPluginId(List<String> sortedPluginId) {
        this.sortedPluginIds = sortedPluginIds;
    }


    public boolean isDisabled(String pluginId){
        return disabledPluginIds != null && disabledPluginIds.contains(pluginId);
    }


    /**
     * 得到禁用的插件id
     * @return Set<String>
     */
    public Set<String> getDisabledPluginIds(){
        return disabledPluginIds;
    }

    /**
     * 得到按照循序启动的插件id
     * @return Set<String>
     */
    public List<String> getSortedPluginIds(){
        return sortedPluginIds;
    }

}
