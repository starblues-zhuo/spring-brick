package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.ExtensionConfigUtils;
import com.gitee.starblues.extension.mybatis.MapperHandler;
import com.gitee.starblues.extension.mybatis.PluginFollowCoreConfig;
import com.gitee.starblues.extension.mybatis.PluginResourceFinder;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;


/**
 * springboot-mybatis plus 处理者
 * @author starBlues
 * @version 2.3
 */
public class MybatisPlusProcessor implements PluginPipeProcessorExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusProcessor.class);

    private final GenericApplicationContext applicationContext;
    private final MapperHandler mapperHandler;

    public MybatisPlusProcessor(ApplicationContext applicationContext) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.mapperHandler = new MapperHandler(this.applicationContext);
    }

    @Override
    public String key() {
        return "MybatisPlusProcessor";
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();

        SpringBootMybatisPlusConfig config = ExtensionConfigUtils.getConfig(applicationContext,
                pluginWrapper.getPluginId(),
                SpringBootMybatisPlusConfig.class);
        if(config == null){
            return;
        }

        final MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();

        if(config.enableOneselfConfig()){
            config.oneselfConfig(factory);
        } else {
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(applicationContext);
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
            mybatisPlusFollowCoreConfig(factory);
        }

        PluginResourceFinder pluginResourceFinder = new PluginResourceFinder(pluginWrapper.getPluginClassLoader());

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
            Resources.setDefaultClassLoader(pluginWrapper.getPluginClassLoader());
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


    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        mapperHandler.unRegistryMapper(pluginRegistryInfo);
    }

    private void mybatisPlusFollowCoreConfig(MybatisSqlSessionFactoryBean factory){

        if(this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length == 0){
            return;
        }

        MybatisPlusProperties plusProperties = applicationContext.getBean(MybatisPlusProperties.class);

        GlobalConfig globalConfig = plusProperties.getGlobalConfig();

        if (this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length > 0) {
            IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }

        if (this.applicationContext.getBeanNamesForType(MetaObjectHandler.class,
                false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = this.applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        if (this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length > 0) {
            IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }

        if (this.applicationContext.getBeanNamesForType(ISqlInjector.class, false,
                false).length > 0) {
            ISqlInjector iSqlInjector = this.applicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(iSqlInjector);
        }
        factory.setGlobalConfig(globalConfig);
    }


}
