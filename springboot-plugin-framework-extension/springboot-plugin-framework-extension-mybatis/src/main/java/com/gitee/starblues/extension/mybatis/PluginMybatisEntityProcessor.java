package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
import com.gitee.starblues.extension.mybatis.utils.TypeAliasRegistryUtils;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实体类别名处理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisEntityProcessor implements PluginPipeProcessorExtend {

    private final static Logger LOG = LoggerFactory.getLogger(PluginMybatisEntityProcessor.class);

    private final static String KEY = "PluginMybatisEntityProcessor";
    private final static String ALIAS_NAME_KEY = "mybatisAliasName";

    private final SqlSessionFactory sqlSessionFactory;

    PluginMybatisEntityProcessor(ApplicationContext mainApplicationContext){
        sqlSessionFactory = mainApplicationContext.getBean(SqlSessionFactory.class);
    }

    @Override
    public String key() {
        return KEY;
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
        if(sqlSessionFactory == null){
            LOG.warn("Mybatis SqlSessionFactory is null, Cannot register alias");
            return;
        }
        TypeAliasRegistry typeAliasRegistry = getTypeAliasRegistry();
        if (typeAliasRegistry == null){
            return;
        }
        Set<String> mybatisAliasNames = pluginRegistryInfo.getExtension(ALIAS_NAME_KEY);
        if(mybatisAliasNames == null){
            mybatisAliasNames = new HashSet<>();
            pluginRegistryInfo.addExtension(ALIAS_NAME_KEY, mybatisAliasNames);
        }
        Map<String, Class<?>> typeAliases = TypeAliasRegistryUtils.getTypeAliases(typeAliasRegistry);
        if(typeAliases == null){
            LOG.warn("Not found mybatis typeAliases Map");
            return;
        }
        processEntityClass(pluginRegistryInfo, typeAliases, mybatisAliasNames);
        processAliasMapping(pluginRegistryInfo, typeAliases, mybatisAliasNames);
    }

    private TypeAliasRegistry getTypeAliasRegistry() {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        if(configuration == null){
            LOG.warn("Mybatis Configuration is null, Cannot register alias");
            return null;
        }
        TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        if(typeAliasRegistry == null){
            LOG.warn("Mybatis TypeAliasRegistry is null, Cannot register alias");
            return null;
        }
        return typeAliasRegistry;
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        TypeAliasRegistry typeAliasRegistry = getTypeAliasRegistry();
        if(typeAliasRegistry == null){
            return;
        }
        Map<String, Class<?>> typeAliases = TypeAliasRegistryUtils.getTypeAliases(typeAliasRegistry);
        Set<String> mybatisAliasNames = pluginRegistryInfo.getExtension(ALIAS_NAME_KEY);
        if(mybatisAliasNames != null && !mybatisAliasNames.isEmpty()){
            for (String mybatisAliasName : mybatisAliasNames) {
                typeAliases.remove(mybatisAliasName);
            }
        }
    }


    /**
     * 处理别名的实体类
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliases mybatis 中内置的别名 Map集合
     * @param mybatisAliasNames 插件中别名存储集合, 用于卸载时使用
     */
    private void processEntityClass(PluginRegistryInfo pluginRegistryInfo,
                                    Map<String, Class<?>> typeAliases,
                                    Set<String> mybatisAliasNames){
        List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(PluginEntityAliasesGroup.KEY);
        if(groupClasses == null || groupClasses.isEmpty()){
            return;
        }
        for (Class<?> groupClass : groupClasses) {
            if(groupClass == null){
                continue;
            }
            String aliasName = lowerFirstChar(groupClass.getSimpleName());
            Alias alias = groupClass.getAnnotation(Alias.class);
            if(alias != null && !StringUtils.isEmpty(alias.value())){
                aliasName = alias.value();
            }
            if(StringUtils.isEmpty(aliasName)){
                continue;
            }
            registerAlias(typeAliases, aliasName, groupClass);
            mybatisAliasNames.add(aliasName);
        }
    }

    /**
     * 注册自定义配置的 aliasMapping 的别名
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliases mybatis 中内置的别名 Map集合
     * @param mybatisAliasNames 插件中别名存储集合, 用于卸载时使用
     */
    private void processAliasMapping(PluginRegistryInfo pluginRegistryInfo,
                                     Map<String, Class<?>> typeAliases,
                                     Set<String> mybatisAliasNames){
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if(basePlugin instanceof SpringBootMybatisConfig){
            SpringBootMybatisConfig config = (SpringBootMybatisConfig) basePlugin;
            Map<String, Class> aliasMapping = config.aliasMapping();
            if(aliasMapping == null || aliasMapping.isEmpty()){
                return;
            }
            aliasMapping.forEach((k, v)->{
                registerAlias(typeAliases, k, v);
                mybatisAliasNames.add(k);
            });
        }
    }


    /**
     * 注册别名。
     * @param typeAliases  mybatis 中内置的别名 Map集合
     * @param alias 别名名称
     * @param value 别名对应的class类
     */
    private void registerAlias(Map<String, Class<?>> typeAliases,
                               String alias,
                               Class<?> value){
        if(StringUtils.isEmpty(alias)){
            return;
        }
        typeAliases.put(alias, value);
    }


    /**
     * 首字母小写
     * @param str 字符串
     * @return 首字母小写的字符串
     */
    private String lowerFirstChar(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
