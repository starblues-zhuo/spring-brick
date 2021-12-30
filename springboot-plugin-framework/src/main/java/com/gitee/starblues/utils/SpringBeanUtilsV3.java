package com.gitee.starblues.utils;

import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class SpringBeanUtilsV3 {

    /**
     * 得到ApplicationContext中的bean的实现
     * @param applicationContext applicationContext
     * @param aClass 接口或者抽象类型bean类型
     * @param <T> 接口或者抽象类型bean类型
     * @return 所有的实现对象
     */
    public static <T> List<T> getBeans(ApplicationContext applicationContext, Class<T> aClass) {
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        Map<String, T> beansOfTypeMap = springBeanFactory.getBeansOfType(aClass);
        if(beansOfTypeMap.isEmpty()){
            return new ArrayList<>();
        }
        return new ArrayList<>(beansOfTypeMap.values());
    }

    /**
     * 得到某个接口的实现对象
     * @param sourceObject 遍历的对象
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型
     * @return 实现对象
     */
    public static <T> T getObjectByInterfaceClass(Set<Object> sourceObject, Class<T> interfaceClass){
        if(sourceObject == null || sourceObject.isEmpty()){
            return null;
        }
        for (Object configSingletonObject : sourceObject) {
            Set<Class<?>> allInterfacesForClassAsSet = ClassUtils
                    .getAllInterfacesAsSet(configSingletonObject);
            if(allInterfacesForClassAsSet.contains(interfaceClass)){
                return (T) configSingletonObject;
            }
        }
        return null;
    }

    /**
     * 获取具体类的对象
     * @param sourceObject 源对象集合
     * @param aClass 对象对应的类类型
     * @param <T> 类实现
     * @return T
     */
    public static <T> T getObjectClass(Set<Object> sourceObject, Class<T> aClass){
        if(sourceObject == null || sourceObject.isEmpty()){
            return null;
        }
        for (Object configSingletonObject : sourceObject) {
            if(Objects.equals(configSingletonObject.getClass(), aClass)){
                return (T) configSingletonObject;
            }
        }
        return null;
    }

    /**
     * 得到存在的bean, 不存在则返回null
     * @param applicationContext applicationContext
     * @param aClass bean 类型
     * @param <T> bean 类型
     * @return 存在bean对象, 不存在返回null
     */
    public static <T> T getExistBean(ApplicationContext applicationContext, Class<T> aClass){
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        String[] beanNamesForType = springBeanFactory.getBeanNamesForType(aClass, false, false);
        if(beanNamesForType.length > 0){
            return springBeanFactory.getBean(aClass);
        } else {
            return null;
        }
    }

    /**
     * 得到存在的bean, 不存在则返回null
     * @param applicationContext applicationContext
     * @param beanName bean 名称
     * @param <T> 返回的bean类型
     * @return 存在bean对象, 不存在返回null
     */
    public static <T> T getExistBean(ApplicationContext applicationContext, String beanName){
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        if(springBeanFactory.containsBean(beanName)){
            Object bean = springBeanFactory.getBean(beanName);
            return (T) bean;
        } else {
            return null;
        }
    }

}
