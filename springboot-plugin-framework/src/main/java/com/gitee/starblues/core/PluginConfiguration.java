package com.gitee.starblues.core;

import java.util.List;
import java.util.Set;

/**
 * 配置
 * @author starBlues
 * @version 3.0.0
 */
public class PluginConfiguration {

    private Set<String> disabledPluginId;
    private List<String> sortedPluginId;

    public void setDisabledPluginIds(Set<String> disabledPluginId) {
        this.disabledPluginId = disabledPluginId;
    }

    public void setSortedPluginId(List<String> sortedPluginId) {
        this.sortedPluginId = sortedPluginId;
    }

    /**
     * 得到禁用的插件id
     * @return Set<String>
     */
    public Set<String> getDisabledPluginId(){
        return disabledPluginId;
    }

    /**
     * 得到按照循序启动的插件id
     * @return Set<String>
     */
    public List<String> getSortedPluginId(){
        return sortedPluginId;
    }

}
