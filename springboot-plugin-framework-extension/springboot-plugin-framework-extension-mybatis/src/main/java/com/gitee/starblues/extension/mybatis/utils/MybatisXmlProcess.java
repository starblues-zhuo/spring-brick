package com.gitee.starblues.extension.mybatis.utils;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis xml 操作者
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class MybatisXmlProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisXmlProcess.class);

    private static MybatisXmlProcess mybatisXmlProcess = null;

    private final Map<String, List<PluginMybatisXmlMapperBuilder>> XML_MAPPER_BUILDERS;
    private final SqlSessionFactory factory;

    private MybatisXmlProcess(SqlSessionFactory sqlSessionFactory) {
        this.factory = sqlSessionFactory;
        this.XML_MAPPER_BUILDERS = new ConcurrentHashMap<>();
    }

    /**
     * 得到单例
     * @param sqlSessionFactory sqlSessionFactory
     * @return MybatisXmlProcess
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
    public void loadXmlResource(String pluginId,
                                List<Resource> resources,
                                ClassLoader pluginClassLoader) throws Exception {
        if(resources == null || resources.isEmpty()){
            return;
        }
        Configuration configuration = factory.getConfiguration();
        ClassLoader defaultClassLoader = Resources.getDefaultClassLoader();
        try {
            Resources.setDefaultClassLoader(pluginClassLoader);
            List<PluginMybatisXmlMapperBuilder> pluginMybatisXmlMapperBuilders = XML_MAPPER_BUILDERS.get(pluginId);
            if(pluginMybatisXmlMapperBuilders == null){
                pluginMybatisXmlMapperBuilders = new ArrayList<>();
                XML_MAPPER_BUILDERS.put(pluginId, pluginMybatisXmlMapperBuilders);
            }
            for (Resource resource : resources) {
                InputStream inputStream = resource.getInputStream();
                try {
                    PluginMybatisXmlMapperBuilder xmlMapperBuilder = new PluginMybatisXmlMapperBuilder(
                            inputStream,
                            configuration,
                            resource.getFilename(),
                            configuration.getSqlFragments(),
                            pluginClassLoader);
                    pluginMybatisXmlMapperBuilders.add(xmlMapperBuilder);
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

    public void unRegistry(String pluginId){
        List<PluginMybatisXmlMapperBuilder> pluginMybatisXmlMapperBuilders = XML_MAPPER_BUILDERS.get(pluginId);
        if(pluginMybatisXmlMapperBuilders == null){
            return;
        }
        for (PluginMybatisXmlMapperBuilder pluginMybatisXmlMapperBuilder : pluginMybatisXmlMapperBuilders) {
            try {
                pluginMybatisXmlMapperBuilder.unRegistry();
            } catch (Exception e){
                LOGGER.warn("UnRegistry xml type alias cache class error of plugin '{}', error '{}'",
                        pluginId, e.getMessage());
            }
        }
    }

}
