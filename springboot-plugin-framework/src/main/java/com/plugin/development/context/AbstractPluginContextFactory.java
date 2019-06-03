package com.plugin.development.context;

import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.realize.PluginApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 09:11
 * @Update Date Time:
 * @see
 */
public abstract class AbstractPluginContextFactory<T extends ApplicationContext> implements PluginContextFactory{



    @Override
    public void registry(String pluginId, PluginApplicationContext pluginApplicationContext)
            throws PluginBeanFactoryException {
        if(StringUtils.isEmpty(pluginId)){
            throw new PluginBeanFactoryException("pluginId can not be empty");
        }
        ApplicationContext applicationContext = isNull(pluginApplicationContext);
        if(!applicationContext.getClass().equals(supportApplicationContextClass())){
            throw new PluginBeanFactoryException("Does not support pluginApplicationContext : " +
                    pluginApplicationContext.getClass().getName() + " -> " + applicationContext.getClass().getName());
        }
        registry(pluginId, (T)applicationContext);
    }

    /**
     * 注册
     * @param pluginId 插件id
     * @param pluginApplicationContext 插件中的applicationContext
     * @throws PluginBeanFactoryException
     */
    public abstract void registry(String pluginId, T pluginApplicationContext) throws PluginBeanFactoryException;


    /**
     * 是否为空
     * @param pluginApplicationContext
     * @return
     */
    private ApplicationContext isNull(PluginApplicationContext pluginApplicationContext){
        Objects.requireNonNull(pluginApplicationContext, "pluginApplicationContext can not be null");
        ApplicationContext applicationContext = pluginApplicationContext.getApplicationContext();
        Objects.requireNonNull(applicationContext, "pluginApplicationContext->" +
                "getApplicationContext can not be null");
        return applicationContext;
    }




}
