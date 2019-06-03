package com.plugin.development.realize;

import java.util.Objects;

/**
 * @Description: 插件配置的参数定义
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 16:53
 * @Update Date Time:
 * @see
 */
public class PluginConfigDefinition {

    /**
     *  插件中的配置文件名称
     */
    private String fileName;

    /**
     * 配置文件实现类的Class定义
     */
    private Class<?> configClass;


    public PluginConfigDefinition(String fileName, Class<?> configClass) {
        this.fileName = fileName;
        this.configClass = configClass;
    }

    public String getFileName() {
        return fileName;
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof PluginConfigDefinition)){
            return false;
        }
        PluginConfigDefinition that = (PluginConfigDefinition) o;
        return getFileName().equals(that.getFileName()) &&
                getConfigClass().equals(that.getConfigClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileName(), getConfigClass());
    }
}
