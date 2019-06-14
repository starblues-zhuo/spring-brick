package com.plugin.development.context.process;

import com.plugin.development.exception.PluginBeanFactoryException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 插件后置处理接口
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.context.process.PluginConfigProcess
 */
public interface PluginPostBeanProcess {

    /**
     * 处理
     * @param bean 当前注册的bean
     * @param pluginApplicationContext 插件上下文
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void process(Object bean, AnnotationConfigApplicationContext pluginApplicationContext)
            throws PluginBeanFactoryException;

    /**
     * 执行顺序
     * @return 返回执行顺序。数字越小越先执行
     */
    default int order(){
        return 0;
    }
}
