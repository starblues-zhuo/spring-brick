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

package com.gitee.starblues.integration.user;

import java.util.Map;

/**
 * bean包装类
 * @author starBlues
 * @version 3.0.0
 */
public class BeanWrapper<T> {

    /**
     * 主程序bean
     */
    private final T mainBean;

    /**
     * 插件bean. key为插件id
     */
    private final Map<String, T> pluginBean;

    public BeanWrapper(T mainBean, Map<String, T> pluginBean) {
        this.mainBean = mainBean;
        this.pluginBean = pluginBean;
    }

    public T getMainBean() {
        return mainBean;
    }

    public Map<String, T> getPluginBean() {
        return pluginBean;
    }
}
