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

package com.gitee.starblues.plugin.pack.dev;

import lombok.Data;

import java.util.List;

/**
 * 开发模式配置
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class DevConfig {

    /**
     * 当前项目依赖其他模块的定义。
     * 主要定义依赖模块target->classes的目录, 方便开发调试
     */
    private List<Dependency> moduleDependencies;

}
