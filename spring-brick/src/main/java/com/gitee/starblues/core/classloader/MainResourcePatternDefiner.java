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

package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 主程序定义者, 从主程序加载资源的定义者
 * @author starBlues
 * @version 3.0.0
 */
public interface MainResourcePatternDefiner {

    /**
     * 包含资源名称.
     * @return 资源名称集合
     */
    Set<String> getIncludePatterns();

    /**
     * 排除资源
     * @return String
     */
    Set<String> getExcludePatterns();



}
