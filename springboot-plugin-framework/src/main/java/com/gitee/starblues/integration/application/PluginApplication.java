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

package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.PluginUser;
import org.springframework.context.ApplicationContext;

/**
 * 插件应用。
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginApplication{

    /**
     * 初始化
     * @param applicationContext Spring上下文
     * @param listener 插件初始化监听者
     */
    void initialize(ApplicationContext applicationContext, PluginInitializerListener listener);


    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginOperator getPluginOperator();

    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginUser getPluginUser();
}
