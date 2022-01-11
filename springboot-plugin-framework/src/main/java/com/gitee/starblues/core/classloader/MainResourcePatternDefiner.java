package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 主程序定义者, 从主程序加载资源的定义者
 * @author starBlues
 * @version 3.0.0
 */
public interface MainResourcePatternDefiner {

    /**
     * 包含资源名称.
     * @return 资源名称集合
     */
    Set<String> getIncludePatterns();

    /**
     * 排除资源
     * @return String
     */
    Set<String> getExcludePatterns();



}
