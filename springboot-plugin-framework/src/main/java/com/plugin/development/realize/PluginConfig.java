package com.plugin.development.realize;

import org.pf4j.ExtensionPoint;

import java.util.Objects;
import java.util.Set;

/**
 * @Description: 插件中自定义的配置文件。注意该插件在开发环境下，只能定义在 resources 目录下。否在在运行时，会找不到该文件。
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 13:23
 * @Update Date Time:
 * @see
 */
public interface PluginConfig extends ExtensionPoint {

    /**
     * 插件配置集合
     * @return
     */
    Set<PluginConfigDefinition> configDefinitions();


}
