package com.gitee.starblues.integration.user;

import com.gitee.starblues.factory.PluginInfoContainer;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认插件使用者
 * @author zhangzhuo
 * @version 2.2.2
 */
public class DefaultPluginUser implements PluginUser{

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final GenericApplicationContext applicationContext;
    protected final PluginManager pluginManager;

    public DefaultPluginUser(ApplicationContext applicationContext, PluginManager pluginManager) {
        Objects.requireNonNull(applicationContext, "ApplicationContext can't be null");
        Objects.requireNonNull(pluginManager, "PluginManager can't be null");
        this.applicationContext = (GenericApplicationContext)applicationContext;
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
        Object bean = applicationContext.getBean(name);
        if(bean == null){
            return null;
        }
        return (T) bean;
    }

    @Override
    public <T> T getBean(Class<T> aClass) {
        return applicationContext.getBean(aClass);
    }

    @Override
    public <T> T getPluginBean(String name) {
        if(isPluginBean(name)){
            return getBean(name);
        }
        return null;
    }

    /**
     * 在主程序中定义的接口。插件或者主程序实现该接口。可以该方法获取到实现该接口的所有实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    @Override
    public <T> List<T> getBeans(Class<T> aClass){
        Map<String, T> beansOfTypeMap = applicationContext.getBeansOfType(aClass);
        if(beansOfTypeMap == null){
            return Collections.emptyList();
        }
        return beansOfTypeMap.values()
                .stream()
                .filter(beansOfType-> beansOfTypeMap != null)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getMainBeans(Class<T> aClass) {
        Map<String, T> beansOfTypeMap = applicationContext.getBeansOfType(aClass);
        if(beansOfTypeMap == null){
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<>();
        beansOfTypeMap.forEach((beanName, bean)->{
            if(!isPluginBean(beanName)){
                beans.add(bean);
            }
        });
        return beans;
    }

    /**
     * 在主程序中定义的接口。获取插件中实现该接口的实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    @Override
    public <T> List<T> getPluginBeans(Class<T> aClass) {
        Map<String, T> beansOfTypeMap = applicationContext.getBeansOfType(aClass);
        if(beansOfTypeMap == null){
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<>();
        beansOfTypeMap.forEach((beanName, bean)->{
            if(isPluginBean(beanName)){
                beans.add(bean);
            }
        });
        return beans;
    }

    @Override
    public <T> List<T> getPluginBeans(String pluginId, Class<T> aClass) {
        Map<String, T> beansOfTypeMap = applicationContext.getBeansOfType(aClass);
        if(beansOfTypeMap == null){
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<>();
        beansOfTypeMap.forEach((beanName, bean)->{
            if(PluginInfoContainer.existRegisterBeanName(pluginId, beanName)){
                beans.add(bean);
            }
        });
        return beans;
    }

    @Override
    public <T> T generateNewInstance(T object) {
        if(object == null){
            return null;
        }
        try {
            Object newObject = applicationContext.getDefaultListableBeanFactory()
                    .createBean(object.getClass());
            return (T) newObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    /**
     * 是否是插件中的bean
     * @param beanName bean名称
     * @return boolean
     */
    protected boolean isPluginBean(String beanName){
        if(beanName == null){
            return false;
        }
        return PluginInfoContainer.existRegisterBeanName(beanName);
    }

}
