package com.gitee.starblues.integration.user;

import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 默认插件使用者
 * @author starBlues
 * @version 2.4.0
 */
public class DefaultPluginUser implements PluginUser{

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final GenericApplicationContext parentApplicationContext;

    public DefaultPluginUser(ApplicationContext parentApplicationContext) {
        Objects.requireNonNull(parentApplicationContext, "ApplicationContext can't be null");
        this.parentApplicationContext = (GenericApplicationContext)parentApplicationContext;
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
//        ConfigurableApplicationContext pluginApplicationContext =
//                PluginInfoContainers.getPluginApplicationContext(pluginId);
//        if(pluginApplicationContext == null){
//            return Collections.emptyList();
//        }
//        return SpringBeanUtils.getBeans(pluginApplicationContext, aClass);
        return null;
    }

    @Override
    public List<Object> getPluginBeansWithAnnotation(Class<? extends Annotation> annotationType) {
//        List<ConfigurableApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
//        List<Object> beans = new ArrayList<>();
//        for (ConfigurableApplicationContext pluginApplicationContext : pluginApplicationContexts) {
//            Map<String, Object> beanMap = pluginApplicationContext.getBeansWithAnnotation(annotationType);
//            if(!ObjectUtils.isEmpty(beanMap)){
//                beans.addAll(beanMap.values());
//            }
//        }
//        return beans;
        return null;
    }

    @Override
    public List<Object> getPluginBeansWithAnnotation(String pluginId, Class<? extends Annotation> annotationType) {
//        ConfigurableApplicationContext genericApplicationContext = PluginInfoContainers.getPluginApplicationContext(pluginId);
//        if(genericApplicationContext == null){
//            return Collections.emptyList();
//        }
//        Map<String, Object> beanMap = genericApplicationContext.getBeansWithAnnotation(annotationType);
//        if(!ObjectUtils.isEmpty(beanMap)){
//            return new ArrayList<>(beanMap.values());
//        } else {
//            return Collections.emptyList();
//        }
        return null;
    }

    @Override
    public <T> T generateNewInstance(T object) {
//        if(object == null){
//            return null;
//        }
//        List<ConfigurableApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
//        pluginApplicationContexts.add(parentApplicationContext);
//        Class<?> aClass = object.getClass();
//        for (ConfigurableApplicationContext pluginApplicationContext : pluginApplicationContexts) {
//            try {
//                // 判断是否存在
//                pluginApplicationContext.getBean(aClass);
//                Object newBean = pluginApplicationContext.getBeanFactory()
//                        .createBean(aClass);
//                return (T) newBean;
//            } catch (Exception e){
//                // 忽略
//            }
//        }
        return null;
    }


    private <T> T getBean(String name, boolean haveParent){
//        List<ConfigurableApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
//        if(haveParent){
//            pluginApplicationContexts.add(parentApplicationContext);
//        }
//        for (ConfigurableApplicationContext pluginApplicationContext : pluginApplicationContexts) {
//            if(pluginApplicationContext.containsBean(name)){
//                return (T) pluginApplicationContext.getBean(name);
//            }
//        }
        return null;
    }

    private <T> T getBean(Class<T> aClass,  boolean haveParent) {
//        List<ConfigurableApplicationContext> pluginApplicationContexts = PluginInfoContainers.getPluginApplicationContexts();
//        if(haveParent){
//            pluginApplicationContexts.add(parentApplicationContext);
//        }
//        for (ConfigurableApplicationContext pluginApplicationContext : pluginApplicationContexts) {
//            try {
//                T bean = pluginApplicationContext.getBean(aClass);
//                if(bean != null){
//                    return bean;
//                }
//            } catch (Exception e){
//                // 忽略
//            }
//        }
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
//        List<ConfigurableApplicationContext> pluginApplicationContexts = new ArrayList<>(1);
//
//        if(type == 1){
//            pluginApplicationContexts.add(parentApplicationContext);
//        } else if(type == 2){
//            pluginApplicationContexts.addAll(PluginInfoContainers.getPluginApplicationContexts());
//        } else if(type == 3){
//            pluginApplicationContexts.add(parentApplicationContext);
//            pluginApplicationContexts.addAll(PluginInfoContainers.getPluginApplicationContexts());
//        } else {
//            return Collections.emptyList();
//        }
//
//        List<T> result = new ArrayList<>();
//        for (ConfigurableApplicationContext pluginApplicationContext : pluginApplicationContexts) {
//            List<T> pluginBeans = SpringBeanUtils.getBeans(pluginApplicationContext, aClass);
//            if(!pluginBeans.isEmpty()){
//                result.addAll(pluginBeans);
//            }
//        }
//        return result;
        return null;
    }

}
