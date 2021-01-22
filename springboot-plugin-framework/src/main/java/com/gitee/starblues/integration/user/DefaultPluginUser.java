package com.gitee.starblues.integration.user;

import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;
import com.gitee.starblues.utils.PluginBeanUtils;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.*;

/**
 * 默认插件使用者
 * @author starBlues
 * @version 2.2.2
 */
public class DefaultPluginUser implements PluginUser{

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final GenericApplicationContext parentApplicationContext;

    protected final PluginManager pluginManager;

    public DefaultPluginUser(ApplicationContext parentApplicationContext, PluginManager pluginManager) {
        Objects.requireNonNull(parentApplicationContext, "ApplicationContext can't be null");
        Objects.requireNonNull(pluginManager, "PluginManager can't be null");
        this.parentApplicationContext = (GenericApplicationContext)parentApplicationContext;
        this.pluginManager = pluginManager;
    }

    /**
     * 通过bean名称得到插件的bean。（Spring管理的bean）
     * @param name 插件bean的名称。spring体系中的bean名称。可以通过注解定义，也可以自定义生成。具体可百度
     * @param <T> bean的类型
     * @return 返回bean
     */
    @Override
    public <T> T getBean(String name){
        return getBean(name, true);
    }

    @Override
    public <T> T getBean(Class<T> aClass) {
        return getBean(aClass, true);
    }

    @Override
    public <T> T getPluginBean(String name) {
        return getBean(name, false);
    }

    /**
     * 在主程序中定义的接口。插件或者主程序实现该接口。可以该方法获取到实现该接口的所有实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    @Override
    public <T> List<T> getBeans(Class<T> aClass){
        return getBeans(aClass, 3);
    }

    @Override
    public <T> List<T> getMainBeans(Class<T> aClass) {
        return getBeans(aClass, 1);
    }

    /**
     * 在主程序中定义的接口。获取插件中实现该接口的实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    @Override
    public <T> List<T> getPluginBeans(Class<T> aClass) {
        return getBeans(aClass, 2);
    }

    @Override
    public <T> List<T> getPluginBeans(String pluginId, Class<T> aClass) {
        GenericApplicationContext pluginApplicationContext =
                PluginInfoContainers.getPluginApplicationContext(pluginId);
        if(pluginApplicationContext == null){
            return Collections.emptyList();
        }
        return PluginBeanUtils.getPluginBeans(pluginApplicationContext, aClass);
    }

    @Override
    public <T> T generateNewInstance(T object) {
        if(object == null){
            return null;
        }
        List<GenericApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
        pluginApplicationContexts.add(parentApplicationContext);
        Class<?> aClass = object.getClass();
        for (GenericApplicationContext pluginApplicationContext : pluginApplicationContexts) {
            try {
                // 判断是否存在
                pluginApplicationContext.getBean(aClass);
                Object newBean = pluginApplicationContext.getBeanFactory()
                        .createBean(aClass);
                return (T) newBean;
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }


    /**
     * 得到插件扩展接口实现的bean。（非Spring管理）
     * @param tClass 接口的类
     * @param <T> bean的类型
     * @return 返回bean
     */
    @Override
    public <T> List<T> getPluginExtensions(Class<T> tClass){
        return pluginManager.getExtensions(tClass);
    }


    private <T> T getBean(String name, boolean haveParent){
        List<GenericApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
        if(haveParent){
            pluginApplicationContexts.add(parentApplicationContext);
        }
        for (GenericApplicationContext pluginApplicationContext : pluginApplicationContexts) {
            if(pluginApplicationContext.containsBean(name)){
                return (T) pluginApplicationContext.getBean(name);
            }
        }
        return null;
    }

    private <T> T getBean(Class<T> aClass,  boolean haveParent) {
        List<GenericApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
        if(haveParent){
            pluginApplicationContexts.add(parentApplicationContext);
        }
        for (GenericApplicationContext pluginApplicationContext : pluginApplicationContexts) {
            try {
                T bean = pluginApplicationContext.getBean(aClass);
                if(bean != null){
                    return bean;
                }
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    /**
     * 获取多个bean.
     * @param aClass 接口或者抽象类类类型
     * @param type 1 获取主程序的, 2 获取插件中的, 3 获取所有的
     * @param <T> 类类型
     * @return List
     */
    private <T> List<T> getBeans(Class<T> aClass, int type) {
        List<GenericApplicationContext> pluginApplicationContexts = new ArrayList<>(1);

        if(type == 1){
            pluginApplicationContexts.add(parentApplicationContext);
        } else if(type == 2){
            pluginApplicationContexts.addAll(PluginInfoContainers.getPluginApplicationContexts());
        } else if(type == 3){
            pluginApplicationContexts.add(parentApplicationContext);
            pluginApplicationContexts.addAll(PluginInfoContainers.getPluginApplicationContexts());
        } else {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();
        for (GenericApplicationContext pluginApplicationContext : pluginApplicationContexts) {
            List<T> pluginBeans = PluginBeanUtils.getPluginBeans(pluginApplicationContext, aClass);
            if(!pluginBeans.isEmpty()){
                result.addAll(pluginBeans);
            }
        }
        return result;
    }

}
