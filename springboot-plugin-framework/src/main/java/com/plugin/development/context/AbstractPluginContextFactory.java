package com.plugin.development.context;

import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.realize.PluginApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 抽象的插件上下文工厂
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 09:11
 * @Update Date Time:
 * @see
 */
public abstract class AbstractPluginContextFactory<T extends ApplicationContext> implements PluginContextFactory{


    private final List<PluginSpringBeanListener> pluginSpringBeanListeners = new ArrayList<>();


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


    /**
     * 设置监听者
     * @param pluginOperatorListener
     */
    public void addListener(PluginSpringBeanListener pluginOperatorListener){
        if(pluginOperatorListener != null){
            pluginSpringBeanListeners.add(pluginOperatorListener);
        }
    }

    /**
     * 通知监听器注册
     * @param pluginId 插件id
     */
    protected void notifyRegistry(String pluginId){
        for (PluginSpringBeanListener pluginSpringBeanListener : pluginSpringBeanListeners) {
            try {
                pluginSpringBeanListener.registryEvent(pluginId);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 通知监听器卸载事件
     * @param pluginId 插件id
     */
    protected void notifyUnRegistry(String pluginId){
        for (PluginSpringBeanListener pluginSpringBeanListener : pluginSpringBeanListeners) {
            try {
                pluginSpringBeanListener.unRegistryEvent(pluginId);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
