package com.gitee.starblues.loader.classloader.filter;

import java.util.jar.JarEntry;

/**
 * 包含资源
 * @author starBlues
 * @version 3.0.0
 */
@FunctionalInterface
public interface IncludeResource {

    /**
     * 过滤排除
     * @param jarEntry jarEntry
     * @return boolean
     */
    boolean include(JarEntry jarEntry);

}
