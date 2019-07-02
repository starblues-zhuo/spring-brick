package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.factory.bean.register.name.PluginAnnotationBeanNameGenerator;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import com.gitee.starblues.utils.OrderExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * 插件基本的 bean注册者
 * @author zhangzhuo
 * @see Component
 * @see Service
 * @see Repository
 * @version 1.0
 */
public class PluginBasicBeanRegister extends AbstractPluginBeanRegister<String>{

    private final static Logger LOG = LoggerFactory.getLogger(PluginBasicBeanRegister.class);


    public PluginBasicBeanRegister(ApplicationContext mainApplicationContext) throws PluginBeanFactoryException {
        super(mainApplicationContext);
    }

    @Override
    public String key() {
        return "PluginBasicBeanRegister";
    }

    @Override
    public String registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException {
        if(!AnnotationsUtils.haveAnnotations(aClass, false,
                Component.class, Service.class, Repository.class, Configuration.class)){
            return null;
        }
        return register(basePlugin, aClass);
    }

    @Override
    public void unRegistry(BasePlugin basePlugin, String beanName) throws PluginBeanFactoryException {
        applicationContext.removeBeanDefinition(beanName);
    }

    @Override
    public int order() {
        return OrderExecution.MIDDLE;
    }


    /**
     * 注册bean
     * @param aClass aClass
     * @return bean 名称
     */
    protected String register(BasePlugin basePlugin, Class<?> aClass){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(aClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        processBeanDefinition(basePlugin, beanDefinition);
        BeanNameGenerator beanNameGenerator = new PluginAnnotationBeanNameGenerator(basePlugin.getWrapper().getPluginId());
        String name = beanNameGenerator.generateBeanName(beanDefinition, applicationContext);
        applicationContext.registerBeanDefinition(name, beanDefinition);
        return name;
    }

    /**
     * 处理 BeanDefinition
     * @param beanDefinition BeanDefinition
     * @return BeanDefinition
     */
    protected BeanDefinition processBeanDefinition(BasePlugin basePlugin, BeanDefinition beanDefinition){
        return beanDefinition;
    }


}
