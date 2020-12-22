package com.gitee.starblues.extension.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 插件跟随主程序时, 获取主程序的Mybatis定义的一些配置
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-17
 */
public class PluginFollowCoreConfig {

    private final ApplicationContext applicationContext;

    public PluginFollowCoreConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public DataSource getDataSource(){
        return applicationContext.getBean(DataSource.class);
    }

    public Configuration getConfiguration(SpringBootMybatisExtension.Type type){
        Configuration configuration = new Configuration();
        if(type == SpringBootMybatisExtension.Type.MYBATIS){
            try {
                Map<String, ConfigurationCustomizer> customizerMap = applicationContext.getBeansOfType(ConfigurationCustomizer.class);
                if(!customizerMap.isEmpty()){
                    for (ConfigurationCustomizer customizer : customizerMap.values()) {
                        customizer.customize(configuration);
                    }
                }
            } catch (Exception e){
                // ignore
            }
        }
        return configuration;
    }

    public MybatisConfiguration getMybatisPlusConfiguration(){
        MybatisConfiguration configuration = new MybatisConfiguration();
        try {
            Map<String, com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer> customizerMap =
                    applicationContext.getBeansOfType(com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer.class);
            if(!customizerMap.isEmpty()){
                for (com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer customizer : customizerMap.values()) {
                    customizer.customize(configuration);
                }
            }
        } catch (Exception e){
            // ignore
        }
        return configuration;
    }

    public Interceptor[] getInterceptor(){
        Map<Class<? extends Interceptor>, Interceptor> interceptorMap = new HashMap<>();
        try {
            SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
            // 先从 SqlSessionFactory 工厂中获取拦截器
            List<Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
            if(interceptors != null){
                for (Interceptor interceptor : interceptors) {
                    if(interceptor == null){
                        continue;
                    }
                    interceptorMap.put(interceptor.getClass(), interceptor);
                }
            }
        } catch (Exception e){
            // ignore
        }
        // 再从定义Bean中获取拦截器
        Map<String, Interceptor> beanInterceptorMap = applicationContext.getBeansOfType(Interceptor.class);
        if(!beanInterceptorMap.isEmpty()){
            beanInterceptorMap.forEach((k, v)->{
                // 如果Class一致, 则会覆盖
                interceptorMap.put(v.getClass(), v);
            });
        }
        if(interceptorMap.isEmpty()) {
            return null;
        } else {
            return interceptorMap.values().toArray(new Interceptor[0]);
        }
    }

    public DatabaseIdProvider getDatabaseIdProvider(){
        String[] beanNamesForType = applicationContext.getBeanNamesForType(DatabaseIdProvider.class, false, false);
        if(beanNamesForType.length > 0){
            return applicationContext.getBean(DatabaseIdProvider.class);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public LanguageDriver[] getLanguageDriver(){
        Map<Class<? extends LanguageDriver>, LanguageDriver> languageDriverMap = new HashMap<>();
        try {
            SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
            LanguageDriverRegistry languageRegistry = sqlSessionFactory.getConfiguration()
                    .getLanguageRegistry();
            // 先从 SqlSessionFactory 工厂中获取LanguageDriver
            Field proxyTypesField = ReflectionUtils.findField(languageRegistry.getClass(), "LANGUAGE_DRIVER_MAP");
            Map<Class<? extends LanguageDriver>, LanguageDriver> driverMap = null;
            if(proxyTypesField != null){
                if (!proxyTypesField.isAccessible()) {
                    proxyTypesField.setAccessible(true);
                }
                driverMap = (Map<Class<? extends LanguageDriver>, LanguageDriver>) proxyTypesField.get(languageRegistry);
            }
            if(driverMap != null){
                languageDriverMap.putAll(driverMap);
            }
        } catch (Exception e){
            // ignore
        }
        Map<String, LanguageDriver> beansLanguageDriver = applicationContext.getBeansOfType(LanguageDriver.class);
        if(!beansLanguageDriver.isEmpty()){
            beansLanguageDriver.forEach((k, v)->{
                // 如果Class一致, 则会覆盖
                languageDriverMap.put(v.getClass(), v);
            });
        }
        if(languageDriverMap.isEmpty()){
            return null;
        }
        return languageDriverMap.values().toArray(new LanguageDriver[0]);
    }



}
