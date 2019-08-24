package com.gitee.starblues.factory.process.pipe.bean.configuration;

import java.util.Objects;

/**
 * 插件配置的参数定义
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginConfigDefinition {

    /**
     *  插件中的配置文件名称
     */
    private final String fileName;

    /**
     * 配置文件实现类的Class定义
     */
    private final Class<?> configClass;



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
