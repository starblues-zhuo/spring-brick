package com.gitee.starblues.spring.processor.classgroup;

/**
 * @author starBlues
 * @version 1.0
 */
public abstract class PluginClassGroup {

    protected static String ID_PREFIX = "PluginClassGroup_";

    public static String OTHER_CLASS_GROUP_ID = ID_PREFIX + "otherClass";

    /**
     * 得到全id
     * @param groupId groupId
     * @return String
     */
    static String getFullId(String groupId){
        return ID_PREFIX + groupId;
    }

    /**
     * 组id
     * @return 组id
     */
    public abstract String groupId();

    /**
     * 过滤类。
     * @param aClass 类
     * @return 返回true.说明符合该分组器。false不符合该分组器
     */
    public abstract boolean filter(Class<?> aClass);

}
