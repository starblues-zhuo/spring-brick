package com.gitee.starblues.factory.process.pipe.classs;

/**
 * 插件类分组器
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface PluginClassGroup {

    /**
     * 组id
     * @return 组id
     */
    String groupId();


    /**
     * 过滤类。
     * @param aClass 类
     * @return 返回true.说明符合该分组器。false不符合该分组器
     */
    boolean filter(Class<?> aClass);


}
