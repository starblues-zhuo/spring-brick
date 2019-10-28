package com.gitee.starblues.extension.mybatis.utils;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis xml 操作者
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class MybatisXmlProcess {

    private static MybatisXmlProcess mybatisXmlProcess = null;

    private final Map<String, String> MAPPERS = new ConcurrentHashMap<>();
    private final SqlSessionFactory factory;

    private MybatisXmlProcess(SqlSessionFactory sqlSessionFactory) {
        this.factory = sqlSessionFactory;
    }

    /**
     * 得到单例
     * @param sqlSessionFactory sqlSessionFactory
     * @return
     */
    public static MybatisXmlProcess getInstance(SqlSessionFactory sqlSessionFactory){
        Objects.requireNonNull(sqlSessionFactory);
        if(mybatisXmlProcess==null){
            synchronized (MybatisXmlProcess.class) {
                if (mybatisXmlProcess == null) {
                    mybatisXmlProcess =
                            new MybatisXmlProcess(sqlSessionFactory);
                }
            }
        }
        return mybatisXmlProcess;
    }



    /**
     * 加载xml资源
     * @param resources resources
     * @param pluginClassLoader pluginClassLoader
     * @throws Exception Exception
     */
    public void loadXmlResource(List<Resource> resources, ClassLoader pluginClassLoader) throws Exception {
        if(resources == null || resources.isEmpty()){
            return;
        }
        Configuration configuration = factory.getConfiguration();
        // removeConfig(configuration);
        ClassLoader defaultClassLoader = Resources.getDefaultClassLoader();
        try {
            Resources.setDefaultClassLoader(pluginClassLoader);
            for (Resource resource :resources) {
                InputStream inputStream = resource.getInputStream();
                try {
                    PluginMybatisXmlMapperBuilder xmlMapperBuilder =  new PluginMybatisXmlMapperBuilder(
                            inputStream,
                            configuration, resource.toString(),
                            configuration.getSqlFragments(),
                            pluginClassLoader);
                    xmlMapperBuilder.parse();
                } finally {
                    if(inputStream != null){
                        inputStream.close();
                    }
                }

            }
        } finally {
            ErrorContext.instance().reset();
            Resources.setDefaultClassLoader(defaultClassLoader);
        }
    }

    /**
     * 是否改变
     * @param resources resources
     * @return boolean
     * @throws IOException IOException
     */
    @Deprecated
    public boolean isChange(List<Resource> resources) throws IOException {
        if(resources == null || resources.isEmpty()){
            return false;
        }
        boolean isChanged = false;
        for (Resource resource : resources) {
            String key = resource.getURI().toString();
            String value = getModify(resource);
            if (!value.equals(MAPPERS.get(key))) {
                isChanged = true;
                MAPPERS.put(key, value);
            }
        }
        return isChanged;
    }


    /**
     * 移除配置
     * @param configuration configuration
     * @throws Exception Exception
     */
    @Deprecated
    private void removeConfig(Configuration configuration) throws Exception {
        Class<?> classConfig = configuration.getClass();
        clearMap(classConfig, configuration, "mappedStatements");
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");
        clearSet(classConfig, configuration, "loadedResources");
    }

    /**
     * 清除Map
     * @param classConfig classConfig
     * @param configuration configuration
     * @param fieldName fieldName
     * @throws Exception Exception
     */
    @Deprecated
    private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        ((Map) field.get(configuration)).clear();
    }

    /**
     * 清除Set
     * @param classConfig classConfig
     * @param configuration configuration
     * @param fieldName fieldName
     * @throws Exception Exception
     */
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        ((Set) field.get(configuration)).clear();
    }


    /**
     * 得到修改信息
     * @param resource 资源
     * @return String
     * @throws IOException IOException
     */
    private String getModify(Resource resource) throws IOException {
        return new StringBuilder()
                .append(resource.contentLength())
                .append("-")
                .append(resource.lastModified())
                .toString();
    }

}
