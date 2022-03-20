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

package com.gitee.starblues.utils;

import java.lang.reflect.Field;

/**
 * 文过滤接口
 *
 * @author starBlues
 * @version 3.0.0
 */
@FunctionalInterface
public interface FieldFilter {

    /**
     * 过滤
     *
     * @param field 当前字段
     * @return true 允许, false 不允许
     */
    boolean filter(Field field);


}
