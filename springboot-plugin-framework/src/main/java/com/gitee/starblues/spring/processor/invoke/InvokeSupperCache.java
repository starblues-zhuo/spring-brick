package com.gitee.starblues.spring.processor.invoke;


/**
 * @author starBlues
 * @version 1.0
 */
public interface InvokeSupperCache {

    Object getSupperBean(String pluginId, String supperKey);
    Object getSupperBean(String supperKey);
    void add(String pluginId, SupperCache cache);
    void remove(String pluginId);



}
