package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigurationGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.OneselfListenerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;
import com.gitee.starblues.utils.GlobalRegistryInfo;
import com.gitee.starblues.utils.PluginOperatorInfo;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基础bean注册
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class BasicBeanProcessor implements PluginPipeProcessor {


    private static final String KEY = "BasicBeanProcessor";

    private final static String AOP_BEAN_NAME_INC_NUM = "AOP_BEAN_NAME_INC_NUM";

    private final SpringBeanRegister springBeanRegister;
    private final BeanFactoryAdvisorRetrievalHelper helper;

    public BasicBeanProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
        this.helper = new BeanFactoryAdvisorRetrievalHelper(
                (ConfigurableListableBeanFactory)applicationContext.getAutowireCapableBeanFactory());
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = new HashSet<>();
        List<Class<?>> springComponents = pluginRegistryInfo
                .getGroupClasses(ComponentGroup.GROUP_ID);
        List<Class<?>> springConfigurations = pluginRegistryInfo
                .getGroupClasses(ConfigurationGroup.GROUP_ID);
        List<Class<?>> springRepository = pluginRegistryInfo
                .getGroupClasses(RepositoryGroup.GROUP_ID);
        List<Class<?>> oneselfListener = pluginRegistryInfo.getGroupClasses(OneselfListenerGroup.GROUP_ID);

        register(pluginRegistryInfo, springComponents, beanNames);
        register(pluginRegistryInfo, springConfigurations, beanNames);
        register(pluginRegistryInfo, springRepository, beanNames);
        register(pluginRegistryInfo, oneselfListener, beanNames);
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = pluginRegistryInfo.getProcessorInfo(KEY);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        if(beanNames != null){
            for (String beanName : beanNames) {
                springBeanRegister.unregister(pluginId, beanName);
            }
        }
    }

    /**
     * 往Spring注册bean
     * @param pluginRegistryInfo 插件注册的信息
     * @param classes 要注册的类集合
     * @param beanNames 存储bean名称集合
     */
    private void register(PluginRegistryInfo pluginRegistryInfo,
                          List<Class<?>> classes,
                          Set<String> beanNames){
        if(classes == null || classes.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (Class<?> aClass : classes) {
            if(aClass == null){
                continue;
            }
            String namePrefix = resolveAopClass(pluginId, aClass);
            String beanName = springBeanRegister.register(pluginId, namePrefix, aClass);
            beanNames.add(beanName);
        }
    }

    /**
     * 该方法解决重复安装、卸载插件时, AOP 类无法注入的bug.
     *  无法注入的原因如下：
     *
     *      第一次初始化插件时, 首先spring boot 会将要注入的类依次匹配，如果是代理类的话，则在
     *              AbstractAutoProxyCreator 该类的proxyTypes属性会将生成的代理类缓存下来，并返回，然后将该代理类注入到使用的类中。
     *      第二次访问时, spring boot 会直接返回了缓存下来的代理类。导致注入类和代理类类型不匹配，无法注入。
     *  解决办法: proxyTypes 缓存的key 是通过class、和beanName 生成的。所以每次注册插件时，将代理类的beanName 用AOP_BEAN_NAME_INC_NUM
     *      的递增数字作为前缀，这样每次生成都得都是新代理类。
     * @param aClass 当前要处理的类
     * @return 返回代理类的bean名称前缀
     */
    private String resolveAopClass(String pluginId, Class<?> aClass){
        PluginOperatorInfo operatorPluginInfo = GlobalRegistryInfo.getPluginInstallNum(pluginId);
        if(operatorPluginInfo == null){
            // 操作插件信息为空, 直接返回空
            return null;
        }
        List<Advisor> advisorBeans = helper.findAdvisorBeans();
        List<Advisor> advisorsThatCanApply = AopUtils.findAdvisorsThatCanApply(advisorBeans, aClass);
        if(advisorsThatCanApply.isEmpty()){
            // 如果不是代理类, 则返回 null
            return null;
        }
        // 是代理类
        Object o = GlobalRegistryInfo.getExtension(AOP_BEAN_NAME_INC_NUM);
        AtomicInteger atomicInteger = null;
        if(o instanceof AtomicInteger){
            atomicInteger = (AtomicInteger) o;
        } else {
            atomicInteger = new AtomicInteger(-1);
            GlobalRegistryInfo.addExtension(AOP_BEAN_NAME_INC_NUM, atomicInteger);
        }
        PluginOperatorInfo.OperatorType operatorType = operatorPluginInfo.getOperatorType();
        if(operatorType == PluginOperatorInfo.OperatorType.INSTALL){
            // 现在的操作为安装操作, 则重新给代理类设置递增的 bean 名称编号
            return String.valueOf(atomicInteger.incrementAndGet());
        } else {
            // 现在时启动操作, 则复用上次的编号
            return String.valueOf(atomicInteger.get());
        }
    }


}
