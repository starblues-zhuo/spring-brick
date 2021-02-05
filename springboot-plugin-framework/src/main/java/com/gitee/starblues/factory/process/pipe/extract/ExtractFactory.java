package com.gitee.starblues.factory.process.pipe.extract;

import com.gitee.starblues.annotation.Extract;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展工厂
 * @author starBlues
 * @version 2.4.1
 */
public class ExtractFactory {

    private Map<String, Map<ExtractCoordinate, Object>> extractMap = new ConcurrentHashMap<>();

    private static ExtractFactory EXTRACT_FACTORY = new ExtractFactory();

    private ExtractFactory(){}

    /**
     * 得到全局的扩展工厂
     * @return ExtractFactory
     */
    public static ExtractFactory getInstant(){
        return EXTRACT_FACTORY;
    }

    /**
     * 添加扩展
     * @param pluginId 插件id
     * @param extractObject 扩展的bean
     */
    void add(String pluginId, Object extractObject){
        if(extractObject == null){
            return;
        }
        Extract extract = getExtract(extractObject);
        if(extract == null){
            return;
        }
        Map<ExtractCoordinate, Object> extractObjects = extractMap.computeIfAbsent(pluginId, k -> new ConcurrentHashMap<>());
        extractObjects.put(new ExtractCoordinate(extract), extractObject);
    }

    /**
     * 根据插件id来移除扩展
     * @param pluginId 插件id
     */
    void remove(String pluginId){
        extractMap.remove(pluginId);
    }

    /**
     * 得到扩展
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    public <T> T getExtract(ExtractCoordinate coordinate){
        for (Map<ExtractCoordinate, Object> value : extractMap.values()) {
            Object o = value.get(coordinate);
            if(o != null){
                return (T) o;
            }
        }
        throw new RuntimeException("Not found " + coordinate);
    }

    /**
     * 得到扩展的对象注解
     * @param extractObject 扩展对象
     * @return Extract 注解
     */
    private Extract getExtract(Object extractObject){
        Extract annotation = extractObject.getClass().getAnnotation(Extract.class);
        if(annotation == null){
            return null;
        }
        return annotation;
    }

}
