package com.plugin.development.context.process;

import com.plugin.development.exception.PluginBeanFactoryException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 11:52
 * @Update Date Time:
 * @see
 */
public interface PluginPostBeanProcess {

    /**
     * 处理
     * @param bean
     * @param pluginApplicationContext
     * @throws PluginBeanFactoryException
     */
    void process(Object bean, AnnotationConfigApplicationContext pluginApplicationContext)
            throws PluginBeanFactoryException;

    /**
     * 执行顺序
     * @return
     */
    default int order(){
        return 0;
    }
}
