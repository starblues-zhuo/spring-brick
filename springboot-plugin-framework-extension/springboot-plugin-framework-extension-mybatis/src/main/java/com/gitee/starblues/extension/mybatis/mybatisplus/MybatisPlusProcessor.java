package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.mybatis.CommonRegister;
import com.gitee.starblues.extension.mybatis.MapperHandler;
import com.gitee.starblues.extension.mybatis.PluginFollowCoreConfig;
import com.gitee.starblues.extension.mybatis.PluginResourceFinder;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;


/**
 * springboot-mybatis plus 处理者
 * @author starBlues
 * @version 2.4.1
 */
public class MybatisPlusProcessor implements PluginBeanRegistrarExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusProcessor.class);


    public MybatisPlusProcessor() {
    }

    @Override
    public String key() {
        return "MybatisPlusProcessor";
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringBootMybatisPlusConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                pluginRegistryInfo.getConfigSingletons(),
                SpringBootMybatisPlusConfig.class);
        if(config == null){
            return;
        }
        final MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();

        if(config.enableOneselfConfig()){
            config.oneselfConfig(factory);
        } else {
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(
                    pluginRegistryInfo.getMainApplicationContext()
            );
            MybatisConfiguration mybatisPlusConfiguration = followCoreConfig.getMybatisPlusConfiguration();
            factory.setDataSource(followCoreConfig.getDataSource());
            factory.setConfiguration(mybatisPlusConfiguration);
            Interceptor[] interceptor = followCoreConfig.getInterceptor();
            if(interceptor != null && interceptor.length > 0){
                factory.setPlugins(interceptor);
            }
            DatabaseIdProvider databaseIdProvider = followCoreConfig.getDatabaseIdProvider();
            if(databaseIdProvider != null){
                factory.setDatabaseIdProvider(databaseIdProvider);
            }
            LanguageDriver[] languageDriver = followCoreConfig.getLanguageDriver();
            if(languageDriver != null){
                factory.setScriptingLanguageDrivers(languageDriver);
            }
            // 配置mybatis-plus私有的配置
            GlobalConfig globalConfig = mybatisPlusFollowCoreConfig(factory, pluginRegistryInfo.getMainApplicationContext());
            config.reSetMainConfig(mybatisPlusConfiguration, globalConfig);
        }

        PluginResourceFinder pluginResourceFinder = new PluginResourceFinder(pluginRegistryInfo);

        Class<?>[] aliasesClasses = pluginResourceFinder.getAliasesClasses(config.entityPackage());
        if(aliasesClasses != null && aliasesClasses.length > 0){
            factory.setTypeAliases(aliasesClasses);
        }

        Resource[] xmlResource = pluginResourceFinder.getXmlResource(config.xmlLocationsMatch());
        if(xmlResource != null && xmlResource.length > 0){
            factory.setMapperLocations(xmlResource);
        }
        ClassLoader defaultClassLoader = Resources.getDefaultClassLoader();
        try {
            Resources.setDefaultClassLoader(pluginRegistryInfo.getPluginClassLoader());
            SqlSessionFactory sqlSessionFactory = factory.getObject();
            if(sqlSessionFactory == null){
                throw new Exception("Get mybatis-plus sqlSessionFactory is null");
            }
            SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            MapperHandler mapperHandler = new MapperHandler();
            mapperHandler.processMapper(pluginRegistryInfo, (holder, mapperClass) -> {
                mapperHandler.commonProcessMapper(holder, mapperClass, sqlSessionFactory, sqlSessionTemplate);
            });
            CommonRegister.commonRegister(pluginRegistryInfo, sqlSessionFactory, sqlSessionTemplate);
        } finally {
            Resources.setDefaultClassLoader(defaultClassLoader);
        }

    }



    private GlobalConfig mybatisPlusFollowCoreConfig(MybatisSqlSessionFactoryBean factory,
                                                     GenericApplicationContext mainApplicationContext){
        MybatisPlusProperties plusProperties = mainApplicationContext.getBean(MybatisPlusProperties.class);

        GlobalConfig currentGlobalConfig = new GlobalConfig();
        currentGlobalConfig.setBanner(false);
        GlobalConfig globalConfig = plusProperties.getGlobalConfig();
        if(globalConfig != null){
            currentGlobalConfig.setDbConfig(globalConfig.getDbConfig());
            currentGlobalConfig.setIdentifierGenerator(globalConfig.getIdentifierGenerator());
            currentGlobalConfig.setMetaObjectHandler(globalConfig.getMetaObjectHandler());
            currentGlobalConfig.setSqlInjector(globalConfig.getSqlInjector());
        }
        factory.setGlobalConfig(currentGlobalConfig);
        return currentGlobalConfig;
    }

}
