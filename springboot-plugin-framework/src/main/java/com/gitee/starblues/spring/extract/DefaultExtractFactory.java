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
