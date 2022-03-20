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

package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 插件启动前后介入
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginLaunchInvolved {

    /**
     * 初始化。仅调用一次
     * @param applicationContext 主程序GenericApplicationContext
     * @param configuration 集成配置
     */
    default void initialize(GenericApplicationContext applicationContext, IntegrationConfiguration configuration){}

    /**
     * 启动之前
     * @param descriptor 插件信息
     * @param classLoader 插件classloader
     * @throws Exception 执行异常
     */
    default void before(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception{}

    /**
     * 启动之后
     * @param descriptor 插件信息
     * @param classLoader 插件classloader
     * @param pluginHook 启动成功后插件返回的钩子
     * @throws Exception 执行异常
     */
    default void after(InsidePluginDescriptor descriptor, ClassLoader classLoader,
                       SpringPluginHook pluginHook) throws Exception{}

    /**
     * 启动失败
     * @param descriptor 插件信息
     * @param classLoader 插件classloader
     * @param throwable 异常信息
     * @throws Exception 执行异常
     */
    default void failure(InsidePluginDescriptor descriptor, ClassLoader classLoader, Throwable throwable) throws Exception{}

    /**
     * 关闭的时候
     * @param descriptor 插件信息
     * @param classLoader 插件classloader
     * @throws Exception 执行异常
     */
    default void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception{}

    /**
     * 执行顺序
     * @return OrderPriority
     */
    default OrderPriority order(){
        return OrderPriority.getMiddlePriority();
    }



}
