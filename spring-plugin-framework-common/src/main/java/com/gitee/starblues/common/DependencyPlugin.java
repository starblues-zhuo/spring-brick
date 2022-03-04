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

package com.gitee.starblues.common;

/**
 * 依赖的插件
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface DependencyPlugin {

    /**
     * 依赖插件id
     *
     * @return String
     */
    String getId();

    /**
     * 依赖插件版本. 如果设置为: 0.0.0 表示支持任意版本依赖
     *
     * @return String
     */
    String getVersion();

    /**
     * 是否为必须依赖. 默认: false
     *
     * @return boolean
     */
    Boolean getOptional();

}
