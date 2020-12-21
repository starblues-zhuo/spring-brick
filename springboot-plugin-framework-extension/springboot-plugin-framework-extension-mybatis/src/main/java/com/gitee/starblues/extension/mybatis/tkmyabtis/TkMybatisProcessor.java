package com.gitee.starblues.extension.mybatis.tkmyabtis;

import com.gitee.starblues.extension.ExtensionConfigUtils;
import com.gitee.starblues.extension.mybatis.MapperHandler;
import com.gitee.starblues.extension.mybatis.PluginFollowCoreConfig;
import com.gitee.starblues.extension.mybatis.PluginResourceFinder;
import com.gitee.starblues.extension.mybatis.SpringBootMybatisExtension;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.mapper.MapperFactoryBean;

/**
 * tk-mybatis处理者
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-16
 */
public class TkMybatisProcessor implements PluginPipeProcessorExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(TkMybatisProcessor.class);

    private final GenericApplicationContext applicationContext;
    private final MapperHandler mapperHandler;
    private final MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<Object>();

    public TkMybatisProcessor(ApplicationContext applicationContext) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.mapperHandler = new MapperHandler(this.applicationContext);
    }

    @Override
    public String key() {
        return "TkMybatisProcessor";
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

        SpringBootTkMybatisConfig config = ExtensionConfigUtils.getConfig(applicationContext,
                pluginWrapper.getPluginId(),
                SpringBootTkMybatisConfig.class);
        if(config == null){
            return;
        }

        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

        Config tkConfig = null;
        if(config.enableOneselfConfig()){
            config.oneselfConfig(factory);
            tkConfig = new Config();
            config.oneselfConfig(tkConfig);
        } else {
            PluginFollowCoreConfig followCoreConfig = new PluginFollowCoreConfig(applicationContext);
            factory.setDataSource(followCoreConfig.getDataSource());
            factory.setConfiguration(followCoreConfig.getConfiguration(SpringBootMybatisExtension.Type.TK_MYBATIS));
            Interceptor[] interceptor = followCoreConfig.getInterceptor();
            if(interceptor != null && interceptor.length > 0){
                factory.setPlugins(interceptor);
            }
            DatabaseIdProvider databaseIdProvider = followCoreConfig.getDatabaseIdProvider();
            if(databaseIdProvider != null){
                factory.setDatabaseIdProvider(databaseIdProvider);
            }
            if(applicationContext.getBeanNamesForType(Config.class,
                    false, false).length > 0){
                tkConfig = applicationContext.getBean(Config.class);
            }
        }

        MapperHelper mapperHelper = new MapperHelper();
        if(tkConfig != null){
            mapperHelper.setConfig(tkConfig);
        }

        ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();
        PluginResourceFinder pluginResourceFinder = new PluginResourceFinder(pluginClassLoader);

        Class<?>[] aliasesClasses = pluginResourceFinder.getAliasesClasses(config.entityPackage());
        if(aliasesClasses != null && aliasesClasses.length > 0){
            factory.setTypeAliases(aliasesClasses);
        }

        Resource[] xmlResource = pluginResourceFinder.getXmlResource(config.xmlLocationsMatch());
        if(xmlResource != null && xmlResource.length > 0){
            factory.setMapperLocations(xmlResource);
        }

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
            mapperHandler.processMapper(pluginRegistryInfo, (holder, mapperClass) -> {
                processMapper(holder, mapperClass, mapperHelper, sqlSessionFactory, sqlSessionTemplate);
                // tk需要立即生成创建Mapper
                applicationContext.getBean(mapperClass);
            });
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


    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        mapperHandler.unRegistryMapper(pluginRegistryInfo);
    }


}
