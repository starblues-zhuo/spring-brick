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
 * 静态常量
 * @author starBlues
 * @version 3.0.0
 */
public abstract class Constants {

    private Constants(){}


    /**
     * 禁用所有插件标志
     */
    public final static String DISABLED_ALL_PLUGIN = "*";

    /**
     * 允许所有版本的标志
     */
    public final static String ALLOW_VERSION = "0.0.0";

    /**
     * 加载到主程序依赖的标志
     */
    public final static String LOAD_TO_MAIN_SIGN = "@LOAD_TO_MAIN";

    /**
     * 相对路径符号标志
     */
    public final static String RELATIVE_SIGN = "~";

}
