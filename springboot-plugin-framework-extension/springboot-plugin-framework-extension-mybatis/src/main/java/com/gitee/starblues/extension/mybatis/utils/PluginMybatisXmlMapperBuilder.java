package com.gitee.starblues.extension.mybatis.utils;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

/**
 * 插件 xmlMapper 构造者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisXmlMapperBuilder extends XMLMapperBuilder {

    private final ClassLoader pluginClassLoader;

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
        Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (typeAliases.containsKey(key)) {
            System.out.println("contauin " + alias);
            return typeAliasRegistry.resolveAlias(alias);
        } else {
            try {
                System.out.println("classforname " + alias);
                return (Class<T>) Class.forName(alias, false, pluginClassLoader);
            } catch (ClassNotFoundException e) {
                throw new TypeException("Could not resolve type alias '" + alias + "'.  Cause: " + e, e);
            }
        }
    }
}