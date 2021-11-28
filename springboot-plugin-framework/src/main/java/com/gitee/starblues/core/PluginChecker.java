package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import java.nio.file.Path;

/**
 * 检查插件是否合法
 * @author starBlues
 */
public interface PluginChecker {

    /**
     * 根据路径检查
     * @param path path
     * @throws Exception 检查异常
     */
    void check(Path path) throws Exception;

    /**
     * 检查
     * @param descriptor 插件信息
     * @throws Exception 检查异常
     */
    void check(PluginDescriptor descriptor) throws Exception;

}
