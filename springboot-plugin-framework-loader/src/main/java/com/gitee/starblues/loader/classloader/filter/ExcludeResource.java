package com.gitee.starblues.loader.classloader.filter;

/**
 * @author starBlues
 * @version 3.0.0
 */
@FunctionalInterface
public interface ExcludeResource {

    /**
     * 过滤排除
     * @param name 资源名称
     * @return boolean
     */
    boolean exclude(String name);

}
