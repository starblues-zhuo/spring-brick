package com.gitee.starblues.factory.process.pipe.classs;

import com.gitee.starblues.realize.BasePlugin;

/**
 * 插件类分组器
 *
 * @author starBlues
 * @version 2.1.0
 */
public interface PluginClassGroup {

    String KEY = "PluginClassGroup_";

    String OTHER_CLASS_GROUP_ID = KEY + "otherClass";

    /**
     * 得到全分组id
     * @param groupId 分组id
     * @return String
     */
    static String getFullId(String groupId){
        return KEY + groupId;
    }

    /**
     * 组id
     * @return 组id
     */
    String groupId();

    /**
     * 初始化。每处理一个插件, 该方法调用一次。
     * @param basePlugin 当前插件信息
     */
    void initialize(BasePlugin basePlugin);


    /**
     * 过滤类。
     * @param aClass 类
     * @return 返回true.说明符合该分组器。false不符合该分组器
     */
    boolean filter(Class<?> aClass);


}
