package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.ExtensionConfigUtils;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

/**
 * mybatis 处理者
 * @author starBlues
 * @version 2.3
 */
public class MybatisProcessor implements PluginPipeProcessorExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisProcessor.class);

    private final GenericApplicationContext applicationContext;
    private final MapperHandler mapperHandler;

    public MybatisProcessor(ApplicationContext applicationContext) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.mapperHandler = new MapperHandler(this.applicationContext);
    }

    @Override
    public String key() {
        return "MybatisProcessor";
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();

        SpringBootMybatisConfig config = ExtensionConfigUtils.getConfig(applicationContext, pluginWrapper.getPluginId(),
                SpringBootMybatisConfig.class);
        if(config == null){
            return;
        }

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

        if(config.enableOneselfConfig()){
            config.oneselfConfig(factory);
        } else {
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(applicationContext);
            factory.setDataSource(followCoreConfig.getDataSource());
            Configuration configuration = followCoreConfig.getConfiguration(SpringBootMybatisExtension.Type.MYBATIS);
            factory.setConfiguration(configuration);
            Interceptor[] interceptor = followCoreConfig.getInterceptor();
            if(interceptor != null && interceptor.length > 0){
                factory.setPlugins(interceptor);
            }
            DatabaseIdProvider databaseIdProvider = followCoreConfig.getDatabaseIdProvider();
            if(databaseIdProvider != null){
                factory.setDatabaseIdProvider(databaseIdProvider);
            }
            LanguageDriver[] languageDrivers = followCoreConfig.getLanguageDriver();
            if(languageDrivers != null){
                for (LanguageDriver languageDriver : languageDrivers) {
                    configuration.getLanguageRegistry().register(languageDriver);
                }
            }
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
                throw new Exception("Get mybatis sqlSessionFactory is null");
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


}
