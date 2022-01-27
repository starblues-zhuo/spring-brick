package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;

/**
 * 插件基本检查者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginBasicChecker {

    /**
     * 根据路径检查
     * @param path path
     * @throws Exception 检查异常
     */
    void checkPath(Path path) throws Exception;

    /**
     * 检查插件描述是否合法
     * @param descriptor 插件信息
     * @throws PluginException 检查异常
     */
    void checkDescriptor(PluginDescriptor descriptor) throws PluginException;

}
