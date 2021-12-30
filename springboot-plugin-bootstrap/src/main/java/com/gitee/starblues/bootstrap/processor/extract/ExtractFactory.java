package com.gitee.starblues.bootstrap.processor.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 扩展工厂
 * @author starBlues
 * @version 2.4.4
 */
public class ExtractFactory {

    public static final String MAIN_EXTRACT_KEY = ExtractFactory.class.getName() + UUID.randomUUID().toString();

    private final Map<String, Map<ExtractCoordinate, ExtractWrapper>> extractMap = new ConcurrentHashMap<>();

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
        Map<ExtractCoordinate, ExtractWrapper> extractObjects = extractMap.computeIfAbsent(pluginId, k ->
                new ConcurrentHashMap<>());
        ExtractWrapper extractWrapper = new ExtractWrapper(extractObject, extract.order());
        extractObjects.put(new ExtractCoordinate(extract, extractObject.getClass()), extractWrapper);
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
    @SuppressWarnings("unchecked")
    public <T> T getExtractByCoordinate(ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        int currentOrder = Integer.MIN_VALUE;
        Object currentObject = null;
        for (Map<ExtractCoordinate, ExtractWrapper> value : extractMap.values()) {
            ExtractWrapper extractWrapper = value.get(coordinate);
            if(extractWrapper != null){
                int order = extractWrapper.getOrder();
                if(order > currentOrder){
                    currentObject = extractWrapper.getObject();
                    // fix: https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I430R6
                    currentOrder = order;
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
    @SuppressWarnings("unchecked")
    public <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        Map<ExtractCoordinate, ExtractWrapper> extractCoordinates = extractMap.get(pluginId);
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
    @SuppressWarnings("unchecked")
    public <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate){
        Objects.requireNonNull(coordinate, "ExtractCoordinate can't be null");
        Map<ExtractCoordinate, ExtractWrapper> extractCoordinates = extractMap.get(MAIN_EXTRACT_KEY);
        if(extractCoordinates  == null){
            throw new RuntimeException("Not found " + coordinate + " from main");
        }
        ExtractWrapper extractWrapper = extractCoordinates.get(coordinate);
        if(extractWrapper == null){
            throw new RuntimeException("Not found " + coordinate + " from main");
        }
        return (T) extractWrapper.getObject();
    }

    /**
     * 根据接口类型获取扩展
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getExtractByInterClass(Class<T> interfaceClass){
        if(interfaceClass == null){
            return Collections.emptyList();
        }
        List<ExtractWrapper> extracts = new ArrayList<>();
        for (Map<ExtractCoordinate, ExtractWrapper> value : extractMap.values()) {
            for (ExtractWrapper extractWrapper : value.values()) {
                Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(
                        extractWrapper.getObject().getClass());
                if(allInterfacesForClassAsSet.contains(interfaceClass)){
                    extracts.add(extractWrapper);
                }
            }
        }
        return sort(extracts);
    }

    /**
     * 根据插件id和接口类型获取扩展
     * @param pluginId 插件id
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass){
        if(interfaceClass == null){
            return Collections.emptyList();
        }
        List<ExtractWrapper> extracts = new ArrayList<>();
        Map<ExtractCoordinate, ExtractWrapper> extractCoordinateObjectMap = extractMap.get(pluginId);
        if(extractCoordinateObjectMap == null || extractCoordinateObjectMap.isEmpty()){
            return Collections.emptyList();
        }
        for (ExtractWrapper wrapper : extractCoordinateObjectMap.values()) {
            Object object = wrapper.getObject();
            Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(object.getClass());
            if(allInterfacesForClassAsSet.contains(interfaceClass)){
                extracts.add(wrapper);
            }
        }
        return sort(extracts);
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
        return extractObject.getClass().getAnnotation(Extract.class);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> sort(List<ExtractWrapper> extractWrappers){
        if(ObjectUtils.isEmpty(extractWrappers)){
            return new ArrayList<>(0);
        }
        return extractWrappers.stream()
                .sorted(Comparator.comparing(ExtractWrapper::getOrder,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(extractWrapper -> (T) extractWrapper.getObject())
                .collect(Collectors.toList());
    }

    /**
     * 扩展对象包装类型
     **/
    private static class ExtractWrapper{
        private final Object object;
        private final int order;

        public ExtractWrapper(Object object, int order) {
            this.object = object;
            this.order = order;
        }

        public Object getObject() {
            return object;
        }

        public int getOrder() {
            return order;
        }
    }

}
