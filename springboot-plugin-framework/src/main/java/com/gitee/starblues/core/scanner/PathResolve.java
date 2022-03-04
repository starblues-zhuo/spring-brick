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

package com.gitee.starblues.core.scanner;

import java.nio.file.Path;

/**
 * 从路径中发现合适的插件
 * @author starBlues
 * @version 3.0.0
 */
public interface PathResolve {

    /**
     * 过滤并返回正确的路径
     * @param path 待过滤路径
     * @return  path 处理后的路径, 返回null 表示不可用
     */
    Path resolve(Path path);


}
