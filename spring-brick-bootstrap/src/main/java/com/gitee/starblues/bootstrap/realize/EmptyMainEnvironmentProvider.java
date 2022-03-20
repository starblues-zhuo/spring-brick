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

import java.util.Collections;
import java.util.Map;

/**
 * 主程序配置信息提供者空值实现
 *
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyMainEnvironmentProvider implements MainEnvironmentProvider{
    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public String getString(String name) {
        return null;
    }

    @Override
    public Integer getInteger(String name) {
        return null;
    }

    @Override
    public Long getLong(String name) {
        return null;
    }

    @Override
    public Double getDouble(String name) {
        return null;
    }

    @Override
    public Float getFloat(String name) {
        return null;
    }

    @Override
    public Boolean getBoolean(String name) {
        return null;
    }

    @Override
    public Map<String, Map<String, Object>> getAll() {
        return Collections.emptyMap();
    }
}
