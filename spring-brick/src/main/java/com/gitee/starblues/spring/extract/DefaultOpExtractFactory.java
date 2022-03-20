/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.spring.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认的可扩展的工厂
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultOpExtractFactory implements OpExtractFactory {

    public static final String MAIN_EXTRACT_KEY = ExtractFactory.class.getName() + UUID.randomUUID().toString();

    private final Map<String, Map<ExtractCoordinate, ExtractWrapper>> extractMap = new ConcurrentHashMap<>();


    @Override
    public void addOfMain(Object extractObject) {
        add(MAIN_EXTRACT_KEY, extractObject);
    }

    @Override
    public void add(String pluginId, Object extractObject) {
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

    @Override
    public void remove(String pluginId) {
        extractMap.remove(pluginId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExtractByCoordinate(ExtractCoordinate coordinate) {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate) {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate) {
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

    @Override
    public <T> List<T> getExtractByInterClass(Class<T> interfaceClass) {
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

    @Override
    public <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass) {
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

    @Override
    public <T> List<T> getExtractByInterClassOfMain(Class<T> interfaceClass) {
        return getExtractByInterClass(MAIN_EXTRACT_KEY, interfaceClass);
    }

    @Override
    public Map<String, Set<ExtractCoordinate>> getExtractCoordinates() {
        Map<String, Set<ExtractCoordinate>> extractCoordinateMap = new HashMap<>(extractMap.size());
        extractMap.forEach((k, v)->{
            Set<ExtractCoordinate> extractCoordinates = new HashSet<>(v.size());
            extractCoordinates.addAll(v.keySet());
            extractCoordinateMap.put(k, extractCoordinates);
        });
        return extractCoordinateMap;
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

    private Extract getExtract(Object extractObject){
        return extractObject.getClass().getAnnotation(Extract.class);
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
