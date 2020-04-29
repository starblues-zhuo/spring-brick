package com.gitee.starblues.extension.mybatis.utils;

import org.apache.ibatis.type.TypeAliasRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * mybatis 别名工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class TypeAliasRegistryUtils {

    private final static Logger LOG = LoggerFactory.getLogger(TypeAliasRegistryUtils.class);

    private TypeAliasRegistryUtils(){

    }


    /**
     *
     * 通过反射获取别名注册器 TypeAliasRegistry 中存储别名的 typeAliases Map集合。
     * @param typeAliasRegistry 别名注册器
     * @return typeAliases Map集合。
     * @throws Exception 获取不到异常
     */
    public static Map<String, Class<?>> getTypeAliases(TypeAliasRegistry typeAliasRegistry) throws Exception {
        if(typeAliasRegistry == null){
            throw new Exception("TypeAliasRegistry can not is null");
        }
        Field field = typeAliasRegistry.getClass().getDeclaredField("typeAliases");
        //设置对象的访问权限，保证对private的属性的访问
        field.setAccessible(true);
        Object fieldObject = field.get(typeAliasRegistry);
        if(fieldObject instanceof Map){
            return (Map<String, Class<?>>)fieldObject;
        } else {
            LOG.warn("Not found TypeAliasRegistry typeAliases");
            throw new Exception("Not found TypeAliasRegistry typeAliases");
        }
    }


}
