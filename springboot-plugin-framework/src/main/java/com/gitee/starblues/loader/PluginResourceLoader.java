package com.gitee.starblues.loader;

import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 插件资源加载者统一定义的接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginResourceLoader {

    /**
     * 加载者的key
     * @return String
     */
    String key();


    /**
     * 加载资源
     * @param basePlugin basePlugin
     * @return List
     * @throws Exception Exception
     */
    List<Resource> load(BasePlugin basePlugin) throws Exception;

    /**
     * 执行顺序
     * @return OrderPriority
     */
    OrderPriority order();

}
