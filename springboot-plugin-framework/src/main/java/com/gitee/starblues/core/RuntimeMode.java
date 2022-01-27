package com.gitee.starblues.core;

/**
 * 插件运行环境
 * @author starBlues
 * @version 3.0.0
 */
public enum RuntimeMode {

    /**
     * 开发环境
     */
    DEV("dev"),

    /**
     * 生产环境
     */
    PROD("prod");

    private final String mode;

    RuntimeMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static RuntimeMode byName(String model){
        if(DEV.name().equalsIgnoreCase(model)){
            return RuntimeMode.DEV;
        } else {
            return RuntimeMode.PROD;
        }
    }

    @Override
    public String toString() {
        return mode;
    }

}
