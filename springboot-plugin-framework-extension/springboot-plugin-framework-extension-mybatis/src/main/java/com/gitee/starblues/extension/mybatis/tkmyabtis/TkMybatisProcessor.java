package com.gitee.starblues.extension.mybatis.tkmyabtis;

import com.gitee.starblues.extension.mybatis.*;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.mapper.MapperFactoryBean;

/**
 * tk-mybatis处理者
 * @author starBlues
 * @version 2.4.0
 */
public class TkMybatisProcessor implements PluginBeanRegistrarExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(TkMybatisProcessor.class);

    private final MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<Object>();

    public TkMybatisProcessor() {
    }

    @Override
    public String key() {
        return "TkMybatisProcessor";
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringBootTkMybatisConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                pluginRegistryInfo.getConfigSingletons(),
                SpringBootTkMybatisConfig.class);
        if(config == null){
            return;
        }

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

        Config tkConfig = null;
        if(config.enableOneselfConfig()){
            tkConfig = new Config();
            config.oneselfConfig(factory, tkConfig);
        } else {
            GenericApplicationContext mainApplicationContext = pluginRegistryInfo.getMainApplicationContext();
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(mainApplicationContext);
            factory.setDataSource(followCoreConfig.getDataSource());
            Configuration configuration = followCoreConfig.getConfiguration(SpringBootMybatisExtension.Type.TK_MYBATIS);
            factory.setConfiguration(configuration);
            Interceptor[] interceptor = followCoreConfig.getInterceptor();
            if(interceptor != null && interceptor.length > 0){
                factory.setPlugins(interceptor);
            }
            DatabaseIdProvider databaseIdProvider = followCoreConfig.getDatabaseIdProvider();
            if(databaseIdProvider != null){
                factory.setDatabaseIdProvider(databaseIdProvider);
            }
            if(mainApplicationContext.getBeanNamesForType(Config.class,
                    false, false).length > 0){
                tkConfig = mainApplicationContext.getBean(Config.class);
            }
            config.reSetMainConfig(configuration, tkConfig);
        }

        MapperHelper mapperHelper = new MapperHelper();
        if(tkConfig != null){
            mapperHelper.setConfig(tkConfig);
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
        ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginClassLoader();
        ClassLoader defaultClassLoader = Resources.getDefaultClassLoader();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Resources.setDefaultClassLoader(pluginClassLoader);
            SqlSessionFactory sqlSessionFactory = factory.getObject();
            if(sqlSessionFactory == null){
                throw new Exception("Get tk-mybatis sqlSessionFactory is null");
            }
            SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            // 用于解决Tk中MsUtil的ClassLoader的问题
            Thread.currentThread().setContextClassLoader(pluginClassLoader);
            MapperHandler mapperHandler = new MapperHandler();
            mapperHandler.processMapper(pluginRegistryInfo, (holder, mapperClass) -> {
                processMapper(holder, mapperClass, mapperHelper, sqlSessionFactory, sqlSessionTemplate);
            });
            CommonRegister.commonRegister(pluginRegistryInfo, sqlSessionFactory, sqlSessionTemplate);
        } finally {
            Resources.setDefaultClassLoader(defaultClassLoader);
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    /**
     * 处理Mapper接口
     * @param holder ignore
     * @param mapperClass ignore
     * @param mapperHelper ignore
     * @param sqlSessionFactory ignore
     * @param sqlSessionTemplate ignore
     */
    private void processMapper(BeanDefinitionHolder holder,
                               Class<?> mapperClass,
                               MapperHelper mapperHelper,
                               SqlSessionFactory sqlSessionFactory,
                               SqlSessionTemplate sqlSessionTemplate){
        GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
        definition.getConstructorArgumentValues().addGenericArgumentValue(mapperClass);
        definition.setBeanClass(this.mapperFactoryBean.getClass());
        //设置通用 Mapper
        definition.getPropertyValues().add("mapperHelper", mapperHelper);
        definition.getPropertyValues().add("addToConfig", true);
        definition.getPropertyValues().add("sqlSessionFactory", sqlSessionFactory);
        definition.getPropertyValues().add("sqlSessionTemplate", sqlSessionTemplate);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
    }


}
