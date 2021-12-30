package com.gitee.starblues.spring.extract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class EmptyExtractFactory implements ExtractFactory{
    @Override
    public <T> T getExtractByCoordinate(ExtractCoordinate coordinate) {
        return null;
    }

    @Override
    public <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate) {
        return null;
    }

    @Override
    public <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate) {
        return null;
    }

    @Override
    public <T> List<T> getExtractByInterClass(Class<T> interfaceClass) {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass) {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> getExtractByInterClassOfMain(Class<T> interfaceClass) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Set<ExtractCoordinate>> getExtractCoordinates() {
        return Collections.emptyMap();
    }
}
