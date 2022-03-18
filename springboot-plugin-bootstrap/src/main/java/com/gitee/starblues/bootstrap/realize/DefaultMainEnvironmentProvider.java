/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap.realize;

import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.spring.MainApplicationContext;

import java.util.Map;
import java.util.function.Function;

/**
 * 主程序配置信息提供者默认实现
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultMainEnvironmentProvider implements MainEnvironmentProvider{

    private final MainApplicationContext mainApplicationContext;

    public DefaultMainEnvironmentProvider(MainApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public Object getValue(String name) {
        Map<String, Map<String, Object>> configurableEnvironment = mainApplicationContext.getConfigurableEnvironment();
        if(ObjectUtils.isEmpty(configurableEnvironment)){
            return null;
        }
        for (Map.Entry<String, Map<String, Object>> entry : configurableEnvironment.entrySet()) {
            Map<String, Object> value = entry.getValue();
            Object o = value.get(name);
            if(o != null){
                return o;
            }
        }
        return null;
    }

    @Override
    public String getString(String name) {
        return getValue(name, String::valueOf);
    }

    @Override
    public Integer getInteger(String name) {
        return getValue(name, value -> {
            if(value instanceof Integer){
                return (Integer) value;
            }
            return Integer.parseInt(String.valueOf(value));
        });
    }

    @Override
    public Long getLong(String name) {
       return getValue(name, value -> {
            if(value instanceof Long){
                return (Long) value;
            }
            return Long.parseLong(String.valueOf(value));
       });
    }

    @Override
    public Double getDouble(String name) {
        return getValue(name, value -> {
            if(value instanceof Double){
                return (Double) value;
            }
            return Double.parseDouble(String.valueOf(value));
        });
    }

    @Override
    public Float getFloat(String name) {
        return getValue(name, value -> {
            if(value instanceof Float){
                return (Float) value;
            }
            return Float.parseFloat(String.valueOf(value));
        });
    }

    @Override
    public Boolean getBoolean(String name) {
        return getValue(name, value -> {
            if(value instanceof Boolean){
                return (Boolean) value;
            }
            return Boolean.parseBoolean(String.valueOf(value));
        });
    }

    @Override
    public Map<String, Map<String, Object>> getAll() {
        return mainApplicationContext.getConfigurableEnvironment();
    }

    private <T> T getValue(String name, Function<Object, T> function){
        Object value = getValue(name);
        if(value == null){
            return null;
        }
        return function.apply(value);
    }

}
