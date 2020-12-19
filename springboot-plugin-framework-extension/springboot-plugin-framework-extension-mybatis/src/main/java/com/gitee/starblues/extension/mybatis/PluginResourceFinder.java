package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.group.PluginMapperGroup;
import com.gitee.starblues.extension.mybatis.mybatisplus.MybatisPlusProcessor;
import com.gitee.starblues.factory.PluginInfoContainer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.name.PluginAnnotationBeanNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 插件资源发现者
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-17
 */
public class PluginResourceFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginResourceFinder.class);

    private final static String TYPE_FILE = "file";
    private final static String TYPE_CLASSPATH = "classpath";
    private final static String TYPE_PACKAGE = "package";


    private final ClassLoader classLoader;
    private final ResourcePatternResolver resourcePatternResolver;
    private final MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();


    public PluginResourceFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);;
    }

    /**
     * 获取插件中xml资源
     * @param xmlLocationsMatchSet xml资源匹配集合
     * @return xml Resource 数组
     * @throws IOException 获取xml资源异常
     */
    public Resource[] getXmlResource(Set<String> xmlLocationsMatchSet) throws IOException {
        if(xmlLocationsMatchSet == null || xmlLocationsMatchSet.isEmpty()){
            return null;
        }
        List<Resource> resources = new ArrayList<>();
        for (String xmlLocationsMatch : xmlLocationsMatchSet) {
            if(StringUtils.isEmpty(xmlLocationsMatch)){
                continue;
            }
            List<Resource> loadResources = getXmlResources(xmlLocationsMatch);
            if(loadResources != null && !loadResources.isEmpty()){
                resources.addAll(loadResources);
            }
        }

        if(resources.isEmpty()){
            return null;
        }

        return resources.toArray(new Resource[0]);
    }

    private List<Resource> getXmlResources(String mybatisMapperXmlLocationMatch) throws IOException {
        String[] split = mybatisMapperXmlLocationMatch.split(":");
        if(split.length != 2){
            return null;
        }
        String type = split[0];
        String location = split[1];
        String matchLocation = null;
        if(Objects.equals(type, TYPE_CLASSPATH) || Objects.equals(type, TYPE_FILE)){
            matchLocation = location;
        } else if(Objects.equals(type, TYPE_PACKAGE)){
            matchLocation = location.replace(".", "/");
        }
        if(matchLocation == null){
            LOGGER.error("mybatisMapperXmlLocation {} illegal", mybatisMapperXmlLocationMatch);
            return null;
        }
        try {
            Resource[] resources = resourcePatternResolver.getResources(matchLocation);
            if(resources.length > 0){
                return Arrays.asList(resources);
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("mybatis xml resource '{}' match error : {}", mybatisMapperXmlLocationMatch,
                    e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取插件的实体类及其别名
     * @param packagePatterns 实体类包名
     * @return Class<?>[]
     * @throws IOException 获取医院异常
     */
    public Class<?>[] getAliasesClasses(Set<String> packagePatterns) throws IOException {
        if(packagePatterns == null || packagePatterns.isEmpty()){
            return null;
        }
        Set<Class<?>> aliasesClasses = new HashSet<>();
        for (String packagePattern : packagePatterns) {
            Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = metadataReaderFactory.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = classLoader.loadClass(classMetadata.getClassName());
                    aliasesClasses.add(clazz);
                } catch (Throwable e) {
                    LOGGER.warn("Cannot load the '{}'. Cause by {}", resource, e.toString());
                }
            }
        }
        return aliasesClasses.toArray(new Class<?>[0]);
    }





}
