package com.gitee.starblues.spring.extract;

import java.util.*;

/**
 * 默认的扩展工厂
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultExtractFactory implements ExtractFactory{

    private final ExtractFactory target;

    public DefaultExtractFactory() {
        this.target = new DefaultOpExtractFactory();
    }

    public ExtractFactory getTarget() {
        return target;
    }

    @Override
    public <T> T getExtractByCoordinate(ExtractCoordinate coordinate) {
        return target.getExtractByCoordinate(coordinate);
    }

    @Override
    public <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate) {
        return target.getExtractByCoordinate(pluginId, coordinate);
    }

    @Override
    public <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate) {
        return target.getExtractByCoordinate(coordinate);
    }

    @Override
    public <T> List<T> getExtractByInterClass(Class<T> interfaceClass) {
        return target.getExtractByInterClass(interfaceClass);
    }

    @Override
    public <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass) {
        return target.getExtractByInterClass(pluginId, interfaceClass);
    }

    @Override
    public <T> List<T> getExtractByInterClassOfMain(Class<T> interfaceClass) {
        return target.getExtractByInterClassOfMain(interfaceClass);
    }

    @Override
    public Map<String, Set<ExtractCoordinate>> getExtractCoordinates() {
        return target.getExtractCoordinates();
    }
}
