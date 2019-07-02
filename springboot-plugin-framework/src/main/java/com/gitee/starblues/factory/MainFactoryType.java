package com.gitee.starblues.factory;

/**
 * 主程序中的工厂类型
 *
 * @author zhangzhuo
 * @version 1.0
 */
public enum MainFactoryType implements FactoryType{

    /**
     * 总工厂
     */
    OVERALL("OVERALL"),

    /**
     * bean 工厂
     */
    BEAN("BEAN"),

    /**
     * configuration bean 工厂
     */
    CONFIGURATION_BEAN("CONFIGURATION_BEAN");

    private String key;

    MainFactoryType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
