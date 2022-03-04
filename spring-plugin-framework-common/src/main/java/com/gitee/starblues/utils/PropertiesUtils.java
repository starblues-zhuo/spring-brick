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

package com.gitee.starblues.utils;

import java.util.Properties;

/**
 * 操作 Manifest 工具类
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class PropertiesUtils {

    private PropertiesUtils(){}

    /**
     * 获取值
     *
     * @param properties properties
     * @param key 获取的key
     * @return 获取的值或者null
     */
    public static String getValue(Properties properties, String key){
        return getValue(properties, key, true);
    }

    /**
     * 获取值
     *
     * @param properties properties
     * @param key 获取的key
     * @param notExitsThrowException 如果不存在是否抛出异常
     * @return 获取的值
     */
    public static String getValue(Properties properties, String key, boolean notExitsThrowException){
        boolean throwException = false;
        String value = null;
        try {
            value = properties.getProperty(key);
            if(value == null && notExitsThrowException){
                throwException = true;
            }
        } catch (Throwable e){
            // 忽略
            throwException = true;
        }
        if(throwException){
            throw new IllegalStateException("Not found '" + key + "' config!");
        }
        return value;
    }

}
