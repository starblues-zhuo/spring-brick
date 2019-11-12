package com.gitee.starblues.integration.operator.verify;


import java.nio.file.Path;

/**
 * 插件合法校验接口
 * @author zhangzhuo
 * @version 1.0
 * @see DefaultPluginVerify
 * @see PluginLegalVerify
 */
public interface PluginVerify {

    /**
     * 校验插件包
     * @param path 插件路径
     * @return 返回校验成功的路径
     * @throws Exception 插件异常
     */
    Path verify(Path path) throws Exception;

}
