package com.gitee.starblues.extension.log.config;

import java.util.Set;

/**
 * 日志配置
 * @author sousouki
 * @version 2.4.3
 */
public interface SpringBootLogConfig {

    /**
     * 日志xml配置文件的路径
     * @return Set
     */
    Set<String> logConfigLocations();
}
