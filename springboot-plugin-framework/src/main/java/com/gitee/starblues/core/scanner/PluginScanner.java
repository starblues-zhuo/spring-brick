package com.gitee.starblues.core.scanner;

import java.nio.file.Path;
import java.util.List;

/**
 * 插件扫描者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginScanner {

    /**
     * 从 rootDir 集合中扫描出插件路径
     * @param rootDir 根目录
     * @return 扫描出的目录
     */
    List<Path> scan(List<String> rootDir);


}
