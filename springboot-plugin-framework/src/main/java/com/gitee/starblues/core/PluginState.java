package com.gitee.starblues.core;

/**
 * 插件状态枚举
 * @author starBlues
 * @version 3.0.0
 */
public enum PluginState {

    /**
     * 创建状态
     */
    CREATED("CREATED"),


    /**
     * 禁用状态
     */
    DISABLED("DISABLED"),

    /**
     * 被加载了
     */
    RESOLVED("LOADED"),

    /**
     * 启动状态
     */
    STARTED("STARTED"),

    /**
     * 停止状态
     */
    STOPPED("STOPPED");


    private final String status;

    PluginState(String status) {
        this.status = status;
    }

    public boolean equals(String status) {
        return (this.status.equalsIgnoreCase(status));
    }

    @Override
    public String toString() {
        return status;
    }

    public static PluginState parse(String string) {
        for (PluginState status : values()) {
            if (status.equals(string)) {
                return status;
            }
        }
        return null;
    }

}
