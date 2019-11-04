package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
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

import java.lang.reflect.Field;
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
        processEntityClass(pluginRegistryInfo, typeAliasRegistry, mybatisAliasNames);
        processAliasMapping(pluginRegistryInfo, typeAliasRegistry, mybatisAliasNames);
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
        Set<String> mybatisAliasNames = pluginRegistryInfo.getExtension(ALIAS_NAME_KEY);
        if(mybatisAliasNames != null && !mybatisAliasNames.isEmpty()){
            for (String mybatisAliasName : mybatisAliasNames) {
                Map<String, Class<?>> typeAliases = getTypeAliases(typeAliasRegistry);
                typeAliases.remove(mybatisAliasName);
            }
        }
    }


    /**
     * 处理别名的实体类
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliasRegistry 别名注册器
     */
    private void processEntityClass(PluginRegistryInfo pluginRegistryInfo,
                                    TypeAliasRegistry typeAliasRegistry,
                                    Set<String> mybatisAliasNames){
        List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(PluginEntityAliasesGroup.KEY);
        if(groupClasses == null || groupClasses.isEmpty()){
            return;
        }
        if(typeAliasRegistry == null){
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
            registerAlias(typeAliasRegistry, aliasName, groupClass);
            mybatisAliasNames.add(aliasName);
        }
    }

    /**
     * 注册自定义配置的 aliasMapping 的别名
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliasRegistry 别名注册器
     */
    private void processAliasMapping(PluginRegistryInfo pluginRegistryInfo,
                                     TypeAliasRegistry typeAliasRegistry,
                                     Set<String> mybatisAliasNames){
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if(basePlugin instanceof SpringBootMybatisConfig){
            SpringBootMybatisConfig config = (SpringBootMybatisConfig) basePlugin;
            Map<String, Class> aliasMapping = config.aliasMapping();
            if(aliasMapping == null || aliasMapping.isEmpty()){
                return;
            }
            aliasMapping.forEach((k, v)->{
                registerAlias(typeAliasRegistry, k, v);
                mybatisAliasNames.add(k);
            });
        }
    }


    /**
     * 注册别名。
     * @param typeAliasRegistry 别名注册器
     * @param alias 别名名称
     * @param value 别名对应的class类
     */
    private void registerAlias(TypeAliasRegistry typeAliasRegistry,
                               String alias,
                               Class<?> value){
        if(StringUtils.isEmpty(alias)){
            return;
        }
        Map<String, Class<?>> typeAliases = getTypeAliases(typeAliasRegistry);
        typeAliases.put(alias, value);
    }

    /**
     *
     * 通过反射获取别名注册器 TypeAliasRegistry 中存储别名的 typeAliases Map集合。
     * @param typeAliasRegistry 别名注册器
     * @return typeAliases Map集合。
     */
    private Map<String, Class<?>> getTypeAliases(TypeAliasRegistry typeAliasRegistry) {
        if(typeAliasRegistry == null){
            return null;
        }
        try {
            Field field = typeAliasRegistry.getClass().getDeclaredField("typeAliases");
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            Object fieldObject = field.get(typeAliasRegistry);
            if(fieldObject instanceof Map){
                return (Map<String, Class<?>>)fieldObject;
            } else {
                LOG.warn("Not found TypeAliasRegistry typeAliases");
                return null;
            }
        } catch (Exception e) {
            LOG.error("Found TypeAliasRegistry typeAliases exception. {}", e.getMessage(), e);
            return null;
        }
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
