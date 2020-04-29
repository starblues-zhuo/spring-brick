package com.gitee.starblues.extension.mybatis.utils;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 插件 xmlMapper 构造者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisXmlMapperBuilder extends XMLMapperBuilder {


    private final ClassLoader pluginClassLoader;
    private final Set<String> typeAliasRegistryClassKeys = new HashSet<>();

    public PluginMybatisXmlMapperBuilder(InputStream inputStream,
                                         Configuration configuration,
                                         String resource,
                                         Map<String, XNode> sqlFragments,
                                         ClassLoader pluginClassLoader) {
        super(inputStream, configuration, resource, sqlFragments);
        this.pluginClassLoader = pluginClassLoader;
    }

    public PluginMybatisXmlMapperBuilder(InputStream inputStream,
                                         Configuration configuration,
                                         String resource, Map<String, XNode> sqlFragments,
                                         String namespace,
                                         ClassLoader pluginClassLoader) {
        super(inputStream, configuration, resource, sqlFragments, namespace);
        this.pluginClassLoader = pluginClassLoader;
    }


    @Override
    protected <T> Class<? extends T> resolveAlias(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            Map<String, Class<?>> typeAliases = TypeAliasRegistryUtils.getTypeAliases(typeAliasRegistry);
            String key = alias.toLowerCase(Locale.ENGLISH);
            if (typeAliases.containsKey(key)) {
                return typeAliasRegistry.resolveAlias(alias);
            } else {
                try {
                    Class<T> aClass = (Class<T>) Class.forName(alias, false, pluginClassLoader);
                    typeAliasRegistryClassKeys.add(key);
                    typeAliases.put(key, aClass);
                    return aClass;
                } catch (ClassNotFoundException e) {
                    throw new TypeException("Could not resolve type alias '" + alias + "'.  Cause: " + e, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 卸载时调用，主要是删除typeAliasRegistry中typeAliases所缓存的Class对象
     */
     void unRegistry(){
         try {
             Map<String, Class<?>> typeAliases = TypeAliasRegistryUtils.getTypeAliases(typeAliasRegistry);
             for (String typeAliasRegistryClassKey : typeAliasRegistryClassKeys) {
                 typeAliases.remove(typeAliasRegistryClassKey);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }



}