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

package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.utils.Order;
import com.gitee.starblues.utils.OrderPriority;

/**
 * spring plugin 处理器
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginProcessor extends Order {

    /**
     * 初始化时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void initialize(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 刷新上下文前
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshBefore(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 刷新上下文后
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshAfter(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 启动失败
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void failure(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 关闭容器时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void close(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 执行顺序
     * @return OrderPriority
     */
    @Override
    default OrderPriority order(){
        return OrderPriority.getLowPriority();
    }

    /**
     * 处理器运行模式
     * @return RunMode
     */
    ProcessorContext.RunMode runMode();


}
