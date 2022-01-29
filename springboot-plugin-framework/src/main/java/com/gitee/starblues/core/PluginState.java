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

package com.gitee.starblues.core;

/**
 * 插件状态枚举
 * @author starBlues
 * @version 3.0.0
 */
public enum PluginState {

    /**
     * 被禁用状态
     */
    DISABLED("DISABLED"),

    /**
     * 被加载了
     */
    LOADED("LOADED"),

    /**
     * 启动状态
     */
    STARTED("STARTED"),

    /**
     * 停止状态
     */
    STOPPED("STOPPED");


    private final String status;

    PluginState(String status) {
        this.status = status;
    }

    public boolean equals(String status) {
        return (this.status.equalsIgnoreCase(status));
    }

    @Override
    public String toString() {
        return status;
    }

    public static PluginState parse(String string) {
        for (PluginState status : values()) {
            if (status.equals(string)) {
                return status;
            }
        }
        return null;
    }

}
