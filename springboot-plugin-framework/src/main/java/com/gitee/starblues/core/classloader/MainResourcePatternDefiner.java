package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 主程序定义者, 从主程序加载资源的定义者
 * @author starBlues
 * @version 3.0.0
 */
public interface MainResourcePatternDefiner {

    /**
     * 资源名称.
     * @return 资源名称集合
     */
    Set<String> getIncludeResourcePatterns();


    Set<String> getExcludeResourcePatterns();

    /**
     * spring spi 定义
     * @return spring spi 集合
     */
    Set<String> getSpringFactoriesPatterns();


}
