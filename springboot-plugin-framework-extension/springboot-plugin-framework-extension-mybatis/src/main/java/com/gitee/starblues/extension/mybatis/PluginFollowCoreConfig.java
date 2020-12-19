package com.gitee.starblues.extension.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;

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

    public Configuration getConfiguration(){
        Configuration configuration = new Configuration();
        Map<String, ConfigurationCustomizer> customizerMap = applicationContext.getBeansOfType(ConfigurationCustomizer.class);
        if(!customizerMap.isEmpty()){
            for (ConfigurationCustomizer customizer : customizerMap.values()) {
                customizer.customize(configuration);
            }
        }
        return configuration;
    }

    public MybatisConfiguration getMybatisPlusConfiguration(){
        MybatisConfiguration configuration = new MybatisConfiguration();
        Map<String, ConfigurationCustomizer> customizerMap = applicationContext.getBeansOfType(ConfigurationCustomizer.class);
        if(!customizerMap.isEmpty()){
            for (ConfigurationCustomizer customizer : customizerMap.values()) {
                customizer.customize(configuration);
            }
        }
        return configuration;
    }

    public Interceptor[] getInterceptor(){
        Map<String, Interceptor> interceptorMap = applicationContext.getBeansOfType(Interceptor.class);
        if(interceptorMap.isEmpty()){
            return null;
        }
        return interceptorMap.values().toArray(new Interceptor[0]);
    }

    public DatabaseIdProvider getDatabaseIdProvider(){
        String[] beanNamesForType = applicationContext.getBeanNamesForType(DatabaseIdProvider.class, false, false);
        if(beanNamesForType.length > 0){
            return applicationContext.getBean(DatabaseIdProvider.class);
        }
        return null;
    }


    public LanguageDriver[] getLanguageDriver(){
        Map<String, LanguageDriver> languageDriverMap = applicationContext.getBeansOfType(LanguageDriver.class);
        if(languageDriverMap.isEmpty()){
            return null;
        }
        return languageDriverMap.values().toArray(new LanguageDriver[0]);
    }



}
