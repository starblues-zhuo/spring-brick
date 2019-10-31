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

import java.util.List;
import java.util.Map;

/**
 * 实体类别名处理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisEntityProcessor implements PluginPipeProcessorExtend {

    private final static Logger LOG = LoggerFactory.getLogger(PluginMybatisEntityProcessor.class);

    private final static String KEY = "PluginMybatisEntityProcessor";

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
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(sqlSessionFactory == null){
            LOG.warn("Mybatis SqlSessionFactory is null, Cannot register alias");
            return;
        }
        Configuration configuration = sqlSessionFactory.getConfiguration();
        if(configuration == null){
            LOG.warn("Mybatis Configuration is null, Cannot register alias");
            return;
        }
        TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        if(typeAliasRegistry == null){
            LOG.warn("Mybatis TypeAliasRegistry is null, Cannot register alias");
            return;
        }
        processEntityClass(pluginRegistryInfo, typeAliasRegistry);
        processAliasMapping(pluginRegistryInfo, typeAliasRegistry);
    }

    /**
     * 处理别名的实体类
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliasRegistry 别名注册器
     */
    private void processEntityClass(PluginRegistryInfo pluginRegistryInfo,
                                    TypeAliasRegistry typeAliasRegistry){
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
            typeAliasRegistry.registerAlias(aliasName, groupClass);
        }
    }

    /**
     * 注册自定义配置的 aliasMapping 的别名
     * @param pluginRegistryInfo 注册的插件信息
     * @param typeAliasRegistry 别名注册器
     */
    private void processAliasMapping(PluginRegistryInfo pluginRegistryInfo,
                                     TypeAliasRegistry typeAliasRegistry){
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if(basePlugin instanceof SpringBootMybatisConfig){
            SpringBootMybatisConfig config = (SpringBootMybatisConfig) basePlugin;
            Map<String, Class> aliasMapping = config.aliasMapping();
            if(aliasMapping == null || aliasMapping.isEmpty()){
                return;
            }
            aliasMapping.forEach((k, v)->{
                typeAliasRegistry.registerAlias(k, v);
            });
        }
    }



    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {

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
