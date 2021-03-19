package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

/**
 * mybatis 处理者
 * @author starBlues
 * @version 2.4.0
 */
public class MybatisProcessor implements PluginBeanRegistrarExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisProcessor.class);

    public MybatisProcessor() {
    }

    @Override
    public String key() {
        return "MybatisProcessor";
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringBootMybatisConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                pluginRegistryInfo.getConfigSingletons(),
                SpringBootMybatisConfig.class);
        if(config == null){
            return;
        }

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

        if(config.enableOneselfConfig()){
            config.oneselfConfig(factory);
        } else {
            GenericApplicationContext mainApplicationContext = pluginRegistryInfo.getMainApplicationContext();
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(mainApplicationContext);
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
                throw new Exception("Get mybatis sqlSessionFactory is null");
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


}
