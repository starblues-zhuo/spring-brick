package com.plugin.development.context;

import com.plugin.development.context.factory.PluginBeanRegistry;
import com.plugin.development.context.process.PluginPostBeanProcess;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Set;

/**
 * @Description: 插件上下文
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-31 09:07
 * @Update Date Time:
 * @see
 */
public interface PluginContext {

    /**
     * 得到主程序的ApplicationContext
     * @return
     */
    ApplicationContext getMainApplicationContext();

    /**
     * 得到组件注册者
     * @return
     */
    PluginBeanRegistry<String> getComponentBeanRegistry();


    /**
     * 得到Controller注册者
     * @return
     */
    PluginBeanRegistry<Set<RequestMappingInfo>> getControllerBeanRegistry();

    /**
     * 插件后置bean处理者
     * @return
     */
    List<PluginPostBeanProcess> getPluginPostBeanProcess();

}
