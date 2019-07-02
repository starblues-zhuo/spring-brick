package com.plugin.example.plugin1.config;

import com.gitee.starblues.annotation.ConfigDefinition;

import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:01
 * @Update Date Time:
 * @see
 */
@ConfigDefinition("plugin1.yml")
public class PluginConfig1 {

    private String name;
    private String plugin;
    private Set<String> setString;
    private List<Integer> listInteger;

    private String defaultValue = "defaultValue";

    private SubConfig subConfig;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public Set<String> getSetString() {
        return setString;
    }

    public void setSetString(Set<String> setString) {
        this.setString = setString;
    }

    public List<Integer> getListInteger() {
        return listInteger;
    }

    public void setListInteger(List<Integer> listInteger) {
        this.listInteger = listInteger;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public SubConfig getSubConfig() {
        return subConfig;
    }

    public void setSubConfig(SubConfig subConfig) {
        this.subConfig = subConfig;
    }

    @Override
    public String toString() {
        return "BasePluginExtension{" +
                "name='" + name + '\'' +
                ", plugin='" + plugin + '\'' +
                ", setString=" + setString +
                ", listInteger=" + listInteger +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

}
