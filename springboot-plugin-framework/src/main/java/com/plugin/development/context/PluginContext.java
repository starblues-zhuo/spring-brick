package com.plugin.development.context;

import com.plugin.development.context.factory.PluginBeanRegistry;
import com.plugin.development.context.process.PluginPostBeanProcess;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Set;

/**
 * 插件上下文
 * @author  zhangzhuo
 * @see com.plugin.development.context.DefaultPluginContext
 * @version 1.0
 */
public interface PluginContext {

    /**
     * 得到主程序的ApplicationContext
     * @return 返回主程序上下文
     */
    ApplicationContext getMainApplicationContext();

    /**
     * 得到组件注册者
     * @return 返回插件Component bean 注册者
     */
    PluginBeanRegistry<String> getComponentBeanRegistry();


    /**
     * 得到Controller注册者
     * @return 返回插件Controller 注册者
     */
    PluginBeanRegistry<Set<RequestMappingInfo>> getControllerBeanRegistry();

    /**
     * 插件后置bean处理者
     * @return 返回插件bean后置处理链
     */
    List<PluginPostBeanProcess> getPluginPostBeanProcess();

}
