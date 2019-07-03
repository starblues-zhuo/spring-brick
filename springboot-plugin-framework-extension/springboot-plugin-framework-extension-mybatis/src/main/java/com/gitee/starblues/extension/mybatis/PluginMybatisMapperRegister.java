package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.extension.mybatis.annotation.PluginMapper;
import com.gitee.starblues.extension.mybatis.utils.MybatisInjectWrapper;
import com.gitee.starblues.factory.bean.register.PluginBasicBeanRegister;
import com.gitee.starblues.factory.bean.register.name.PluginAnnotationBeanNameGenerator;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件 mybatis mapper 注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class PluginMybatisMapperRegister extends PluginBasicBeanRegister {

    private final static Logger LOG = LoggerFactory.getLogger(PluginMybatisMapperRegister.class);

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private final MybatisInjectWrapper mybatisInjectWrapper;


    public PluginMybatisMapperRegister(ApplicationContext mainApplicationContext) throws PluginBeanFactoryException {
        super(mainApplicationContext);
        applicationContext.registerBean(MybatisInjectWrapper.class);
        mybatisInjectWrapper = applicationContext.getBean(MybatisInjectWrapper.class);
    }


    @Override
    public String key() {
        return "PluginMybatisMapperRegister";
    }

    @Override
    public String registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException {
        if(!AnnotationsUtils.haveAnnotations(aClass, false,
                PluginMapper.class)){
            return null;
        }
        if(!mybatisExist()){
            return null;
        }
        BeanNameGenerator beanNameGenerator = new PluginAnnotationBeanNameGenerator(basePlugin.getWrapper().getPluginId());
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(aClass);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = beanNameGenerator.generateBeanName(abd, applicationContext);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, applicationContext);
        mybatisInjectWrapper.processBeanDefinitions(definitionHolder, aClass);
        return beanName;
    }

    @Override
    public int order() {
        return super.order() + 20;
    }

    private boolean mybatisExist(){
        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        SqlSessionTemplate sqlSessionTemplate = applicationContext.getBean(SqlSessionTemplate.class);
        return sqlSessionFactory != null && sqlSessionTemplate != null;
    }


}
