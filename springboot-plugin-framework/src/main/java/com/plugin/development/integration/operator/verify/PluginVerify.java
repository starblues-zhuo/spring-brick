package com.plugin.development.integration.operator.verify;

import org.pf4j.PluginException;

import java.nio.file.Path;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-06-01 11:21
 * @Update Date Time:
 * @see
 */
public interface PluginVerify {

    /**
     * 校验插件包
     * @param path 插件路径
     * @return
     * @throws PluginException
     */
    Path verify(Path path) throws PluginException;

}
