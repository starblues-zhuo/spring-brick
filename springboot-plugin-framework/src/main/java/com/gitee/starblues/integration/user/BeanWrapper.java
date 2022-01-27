package com.gitee.starblues.integration.user;

import java.util.Map;

/**
 * bean包装类
 * @author starBlues
 * @version 3.0.0
 */
public class BeanWrapper<T> {

    /**
     * 主程序bean
     */
    private final T mainBean;

    /**
     * 插件bean. key为插件id
     */
    private final Map<String, T> pluginBean;

    public BeanWrapper(T mainBean, Map<String, T> pluginBean) {
        this.mainBean = mainBean;
        this.pluginBean = pluginBean;
    }

    public T getMainBean() {
        return mainBean;
    }

    public Map<String, T> getPluginBean() {
        return pluginBean;
    }
}
