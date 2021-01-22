package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.mybatis.MapperHandler;
import com.gitee.starblues.extension.mybatis.PluginFollowCoreConfig;
import com.gitee.starblues.extension.mybatis.PluginResourceFinder;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.utils.PluginBeanUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;


/**
 * springboot-mybatis plus 处理者
 * @author starBlues
 * @version 2.3
 */
public class MybatisPlusProcessor implements PluginBeanRegistrarExtend, ImportBeanDefinitionRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusProcessor.class);

    private final MapperHandler mapperHandler;

    public MybatisPlusProcessor(ApplicationContext applicationContext) {
        this.mapperHandler = new MapperHandler(applicationContext);
    }

    @Override
    public String key() {
        return "MybatisPlusProcessor";
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringBootMybatisPlusConfig config = PluginBeanUtils.getObjectByInterfaceClass(
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
            factory.setDataSource(followCoreConfig.getDataSource());
            factory.setConfiguration(followCoreConfig.getMybatisPlusConfiguration());
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
            // 配置mybatis私有的配置
            mybatisPlusFollowCoreConfig(factory, pluginRegistryInfo.getMainApplicationContext());
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
            Resources.setDefaultClassLoader(pluginRegistryInfo.getPluginClassLoader(PluginRegistryInfo.ClassLoaderStrategy.PAD));
            SqlSessionFactory sqlSessionFactory = factory.getObject();
            if(sqlSessionFactory == null){
                throw new Exception("Get mybatis-plus sqlSessionFactory is null");
            }
            SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            mapperHandler.processMapper(pluginRegistryInfo, (holder, mapperClass) -> {
                mapperHandler.commonProcessMapper(holder, mapperClass, sqlSessionFactory, sqlSessionTemplate);
            });
        } finally {
            Resources.setDefaultClassLoader(defaultClassLoader);
        }

    }



    private void mybatisPlusFollowCoreConfig(MybatisSqlSessionFactoryBean factory,
                                             GenericApplicationContext parentApplicationContext){
        MybatisPlusProperties plusProperties = parentApplicationContext.getBean(MybatisPlusProperties.class);

        GlobalConfig globalConfig = plusProperties.getGlobalConfig();

        if (parentApplicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length > 0) {
            IKeyGenerator keyGenerator = parentApplicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }

        if (parentApplicationContext.getBeanNamesForType(MetaObjectHandler.class,
                false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = parentApplicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        if (parentApplicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length > 0) {
            IKeyGenerator keyGenerator = parentApplicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }

        if (parentApplicationContext.getBeanNamesForType(ISqlInjector.class, false,
                false).length > 0) {
            ISqlInjector iSqlInjector = parentApplicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(iSqlInjector);
        }
        factory.setGlobalConfig(globalConfig);
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    }
}
