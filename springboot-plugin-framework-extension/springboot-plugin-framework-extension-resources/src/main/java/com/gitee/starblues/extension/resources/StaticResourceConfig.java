package com.gitee.starblues.extension.resources;

import java.util.Set;

/**
 * 插件静态资源的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface StaticResourceConfig {

    /**
     * 静态文件路径
     *  classpath: /static/
     *  file: D://path/test
     * @return 路径集合
     */
    Set<String> locations();



}
