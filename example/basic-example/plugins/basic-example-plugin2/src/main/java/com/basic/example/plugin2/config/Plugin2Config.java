package com.basic.example.plugin2.config;

import com.gitee.starblues.annotation.ConfigDefinition;

import java.util.List;
import java.util.Set;

/**
 * 插件2配置文件对应的bean 定义
 * 必须有ConfigDefinition注解
 * @author zhangzhuo
 * @version 1.0
 */
@ConfigDefinition("plugin2.yml")
public class Plugin2Config {

    private String name;
    private String plugin;
    private Set<String> setString;
    private List<Integer> listInteger;

    private String defaultValue = "defaultValue";

    private Sub2Config sub2Config;




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

    public Sub2Config getSub2Config() {
        return sub2Config;
    }

    public void setSub2Config(Sub2Config sub2Config) {
        this.sub2Config = sub2Config;
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
