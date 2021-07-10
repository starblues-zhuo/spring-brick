package com.gitee.starblues.factory.process.pipe.extract;

import com.gitee.starblues.annotation.Extract;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展工厂
 * @author starBlues
 * @version 2.4.4
 */
public class ExtractFactory {

    public static final String MAIN_EXTRACT_KEY = ExtractFactory.class.getName() + UUID.randomUUID().toString();

    private final Map<String, Map<ExtractCoordinate, Object>> extractMap = new ConcurrentHashMap<>();

    private final static ExtractFactory EXTRACT_FACTORY = new ExtractFactory();

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
     * @param extractObject 扩展的bean
     */
    void addOfMain(Object extractObject){
        add(MAIN_EXTRACT_KEY, extractObject);
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
        extractObjects.put(new ExtractCoordinate(extract, extractObject.getClass()), extractObject);
    }

    /**
     * 根据插件id来移除扩展
     * @param pluginId 插件id
     */
    void remove(String pluginId){
        extractMap.remove(pluginId);
    }

    /**
     * 通过坐标得到扩展
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    public <T> T getExtractByCoordinate(ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        int currentOrder = Integer.MIN_VALUE;
        Object currentObject = null;
        for (Map<ExtractCoordinate, Object> value : extractMap.values()) {
            Object o = value.get(coordinate);
            if(o != null){
                int order = coordinate.getOrder();
                if(order > currentOrder){
                    currentObject = o;
                }
            }
        }
        if(currentObject != null){
            return (T) currentObject;
        }
        throw new RuntimeException("Not found " + coordinate);
    }

    /**
     * 根据插件id和坐标得到扩展
     * @param pluginId 插件id
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    public <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        Map<ExtractCoordinate, Object> extractCoordinates = extractMap.get(pluginId);
        if(extractCoordinates  == null){
            throw new RuntimeException("Not found " + coordinate + " from plugin '" + pluginId + "'");
        }
        Object extracts = extractCoordinates.get(coordinate);
        if(extracts == null){
            throw new RuntimeException("Not found " + coordinate + " from plugin '" + pluginId + "'");
        }
        return (T) extracts;
    }


    /**
     * 根据坐标得到主程序的扩展
     * 主程序扩展必须使用 @Extract+@Component 进行定义
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    public <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        Map<ExtractCoordinate, Object> extractCoordinates = extractMap.get(MAIN_EXTRACT_KEY);
        if(extractCoordinates  == null){
            throw new RuntimeException("Not found " + coordinate + " from main");
        }
        Object extracts = extractCoordinates.get(coordinate);
        if(extracts == null){
            throw new RuntimeException("Not found " + coordinate + " from main");
        }
        return (T) extracts;
    }

    /**
     * 根据接口类型获取扩展
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    public <T> List<T> getExtractByInterClass(Class<T> interfaceClass){
        if(interfaceClass == null){
            return Collections.emptyList();
        }
        List<T> extracts = new ArrayList<>();
        for (Map<ExtractCoordinate, Object> value : extractMap.values()) {
            for (Object o : value.values()) {
                Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(o.getClass());
                if(allInterfacesForClassAsSet.contains(interfaceClass)){
                    extracts.add((T)o);
                }
            }
        }
        return extracts;
    }

    /**
     * 根据插件id和接口类型获取扩展
     * @param pluginId 插件id
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    public <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass){
        if(interfaceClass == null){
            return Collections.emptyList();
        }
        List<T> extracts = new ArrayList<>();
        Map<ExtractCoordinate, Object> extractCoordinateObjectMap = extractMap.get(pluginId);
        if(extractCoordinateObjectMap == null || extractCoordinateObjectMap.isEmpty()){
            return Collections.emptyList();
        }
        for (Object o : extractCoordinateObjectMap.values()) {
            Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(o.getClass());
            if(allInterfacesForClassAsSet.contains(interfaceClass)){
                extracts.add((T)o);
            }
        }
        return extracts;
    }

    /**
     * 根据接口类型获取主程序的扩展
     * 主程序扩展必须使用 @Extract+@Component 进行定义
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    public <T> List<T> getExtractByInterClassOfMain(Class<T> interfaceClass){
        return getExtractByInterClass(MAIN_EXTRACT_KEY, interfaceClass);
    }

    /**
     * 得到所有的扩展坐标
     * @return 扩展坐标集合, key 为插件id, 值为所有扩展坐标集合
     */
    public Map<String, Set<ExtractCoordinate>> getExtractCoordinates(){
        Map<String, Set<ExtractCoordinate>> extractCoordinateMap = new HashMap<>(extractMap.size());
        extractMap.forEach((k, v)->{
            Set<ExtractCoordinate> extractCoordinates = new HashSet<>(v.size());
            extractCoordinates.addAll(v.keySet());
            extractCoordinateMap.put(k, extractCoordinates);
        });
        return extractCoordinateMap;
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
