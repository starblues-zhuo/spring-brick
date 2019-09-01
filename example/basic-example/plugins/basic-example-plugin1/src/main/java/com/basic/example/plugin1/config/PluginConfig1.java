package com.basic.example.plugin1.config;

import com.gitee.starblues.annotation.ConfigDefinition;

import java.util.List;
import java.util.Set;

/**
 * 插件配置文件定义。
 * 必须存在ConfigDefinition注解
 * @author zhangzhuo
 * @version 1.0
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
