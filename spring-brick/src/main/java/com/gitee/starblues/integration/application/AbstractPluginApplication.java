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

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 公用的的插件应用
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginApplication implements PluginApplication {

    /**
     * 子类可通过Application 获取插件定义的配置
     * @param applicationContext applicationContext
     * @return IntegrationConfiguration
     */
    protected IntegrationConfiguration getConfiguration(ApplicationContext applicationContext){
        IntegrationConfiguration configuration = null;
        try {
            configuration = applicationContext.getBean(IntegrationConfiguration.class);
        } catch (Exception e){
            // no show exception
        }
        if(configuration == null){
            throw new BeanCreationException("没有发现 <IntegrationConfiguration> Bean, " +
                    "请在 Spring 容器中将 <IntegrationConfiguration> 定义为Bean");
        }
        return configuration;
    }

}
