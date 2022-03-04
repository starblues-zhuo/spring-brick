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

package com.gitee.starblues.core.version;

/**
 * 版本检查
 * @author starBlues
 * @version 3.0.0
 */
public interface VersionInspector {


    /**
     * 比较 v1 和 v2版本.
     * @param v1 版本号码1
     * @param v2 版本号码2
     * @return 如果 v1大于等于v2, 则返回大于等于0的数字, 否则返回小于0的数字
     */
    int compareTo(String v1, String v2);

}
