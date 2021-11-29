package com.gitee.starblues.integration.user;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 该接口用于在主程序操作Spring管理的插件bean.
 *  主要用途: 在主程序定义接口。插件中实现该接口做扩展, 主程序通过接口class可以获取到插件中的实现类。
 * @author starBlues
 * @version 2.2.2
 */
public interface PluginUser {

    /**
     * 通过bean名称得到bean。（Spring管理的bean）
     * @param name bean的名称。spring体系中的bean名称。可以通过注解定义，也可以自定义生成。具体可百度
     * @param <T> bean的类型
     * @return T
     */
    <T> T getBean(String name);

    /**
     * 通过aClass得到bean。（Spring管理的bean）
     * @param aClass class
     * @param <T> bean的类型
     * @return T
     */
    <T> T getBean(Class<T> aClass);

    /**
     * 通过bean名称得到插件中的bean。（Spring管理的bean）
     * @param name 插件中bean的名称。spring体系中的bean名称。可以通过注解定义，也可以自定义生成。具体可百度
     * @param <T> bean的类型
     * @return T
     */
    <T> T getPluginBean(String name);

    /**
     * 在主程序中定义的接口。
     * 插件或者主程序实现该接口。可以该方法获取到实现该接口的所有实现类。（Spring管理的bean）
     * 使用场景:
     *  1. 在主程序定义接口
     *  2. 在主程序和插件包中都存在实现该接口, 并使用Spring的组件注解(@Component、@Service)
     *  3. 使用该方法可以获取到所以实现该接口的实现类(主程序和插件中)。
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    <T> List<T> getBeans(Class<T> aClass);

    /**
     * 得到主函数中定义的类。
     * 使用场景:
     *  1. 在主程序定义接口
     *  2. 在主程序和插件包中都存在实现该接口, 并使用Spring的组件注解(@Component、@Service)
     *  3. 使用该方法可以获取到主程序实现该接口的实现类。
     * @param aClass 类/接口的类
     * @param <T> bean 的类型
     * @return List
     */
    <T> List<T> getMainBeans(Class<T> aClass);


    /**
     * 在主程序中定义的接口。获取插件中实现该接口的实现类。（Spring管理的bean）
     * 使用场景:
     *  1. 在主程序定义接口
     *  2. 插件包中实现该接口, 并使用Spring的组件注解(@Component、@Service)
     *  3. 使用该方法可以获取到插件中实现该接口的实现类(不包括主程序)。
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return 实现 aClass 接口的实现类的集合
     */
    <T> List<T> getPluginBeans(Class<T> aClass);

    /**
     * 在主程序中定义的接口。获取指定插件中实现该接口的实现类。（Spring管理的bean）
     * 使用场景:
     *  1. 在主程序定义接口
     *  2. 插件包中实现该接口, 并使用Spring的组件注解(@Component、@Service)
     *  3. 使用该方法可以获取到指定插件中实现该接口的实现类。
     * @param pluginId 插件id
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return 实现 aClass 接口的实现类的集合
     */
    <T> List<T> getPluginBeans(String pluginId, Class<T> aClass);

    /**
     * 通过注解获取所有插件中的bean。（Spring管理的bean）
     * @param annotationType 注解类型
     * @return 该注解的bean集合
     */
    List<Object> getPluginBeansWithAnnotation(Class<? extends Annotation> annotationType);

    /**
     * 通过注解获取具体插件中的bean。（Spring管理的bean）
     * @param pluginId  插件id
     * @param annotationType 注解类型
     * @return 该注解的bean集合
     */
    List<Object> getPluginBeansWithAnnotation(String pluginId, Class<? extends Annotation> annotationType);


    /**
     * 生成一个新的Spring实例Bean.
     * 使用场景：主要用于非单例对象的生成。
     * @param object 旧实例对象
     * @param <T> 实例泛型
     * @return 新实例对象
     */
    @Deprecated
    <T> T generateNewInstance(T object);

}
