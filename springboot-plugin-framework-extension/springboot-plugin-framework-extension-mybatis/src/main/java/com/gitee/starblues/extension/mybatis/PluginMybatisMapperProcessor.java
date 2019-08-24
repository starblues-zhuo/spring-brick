package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.utils.MybatisInjectWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.factory.PluginInfoContainer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.bean.name.PluginAnnotationBeanNameGenerator;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件 mybatis mapper 注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class PluginMybatisMapperProcessor implements PluginPipeProcessorExtend {

    private final static Logger LOG = LoggerFactory.getLogger(PluginMybatisMapperProcessor.class);

    private final static String KEY = "PluginMybatisMapperProcessor";

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private final GenericApplicationContext applicationContext;
    private final MybatisInjectWrapper mybatisInjectWrapper;
    private final SpringBeanRegister springBeanRegister;


    PluginMybatisMapperProcessor(ApplicationContext applicationContext) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        springBeanRegister = new SpringBeanRegister(applicationContext);
        springBeanRegister.register(MybatisInjectWrapper.class);
        mybatisInjectWrapper = applicationContext.getBean(MybatisInjectWrapper.class);
    }


    /**
     * 判断mybaits依赖是否存在
     * @return 存在返回true, 不存在返回false
     */
    private boolean mybatisExist(){
        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        SqlSessionTemplate sqlSessionTemplate = applicationContext.getBean(SqlSessionTemplate.class);
        return sqlSessionFactory != null && sqlSessionTemplate != null;
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(!mybatisExist()){
            return;
        }
        List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(PluginMapperGroup.KEY);
        if(groupClasses == null || groupClasses.isEmpty()){
            return;
        }
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        Set<String> beanNames = new HashSet<>();
        for (Class<?> groupClass : groupClasses) {
            if (groupClass == null) {
                continue;
            }
            BeanNameGenerator beanNameGenerator = new PluginAnnotationBeanNameGenerator(basePlugin.getWrapper().getPluginId());
            AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(groupClass);
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
            abd.setScope(scopeMetadata.getScopeName());
            String beanName = beanNameGenerator.generateBeanName(abd, applicationContext);
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
            AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
            BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, applicationContext);
            mybatisInjectWrapper.processBeanDefinitions(definitionHolder, groupClass);
            beanNames.add(beanName);
            PluginInfoContainer.addRegisterBeanName(beanName);
        }
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = pluginRegistryInfo.getProcessorInfo(KEY);
        if(beanNames == null){
            return;
        }
        for (String beanName : beanNames) {
            applicationContext.removeBeanDefinition(beanName);
            PluginInfoContainer.removeRegisterBeanName(beanName);
        }
    }

    @Override
    public String key() {
        return "PluginMybatisMapperProcessor";
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }
}
