package com.gitee.starblues.extension.resources;

import java.util.Set;

/**
 * 插件静态资源的配置
 * 建议从配置文件中进行配置:
 *  plugin:
 *      static:
 *          locations:
 *              - classpath: static/
 *              - file: D://path/test
 * @author starBlues
 * @version 2.3
 */
@Deprecated
public interface StaticResourceConfig {


    /**
     * 静态文件路径
     *  classpath: static/
     *  file: D://path/test
     * @return 路径集合
     */
    Set<String> locations();



}
