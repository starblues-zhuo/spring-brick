package com.plugin.development.integration.operator.verify;

import org.pf4j.PluginException;

import java.nio.file.Path;

/**
 * 插件合法校验接口
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.integration.operator.verify.PluginUploadVerify
 * @see com.plugin.development.integration.operator.verify.PluginLegalVerify
 */
public interface PluginVerify {

    /**
     * 校验插件包
     * @param path 插件路径
     * @return 返回校验成功的路径
     * @throws PluginException 插件异常
     */
    Path verify(Path path) throws PluginException;

}
