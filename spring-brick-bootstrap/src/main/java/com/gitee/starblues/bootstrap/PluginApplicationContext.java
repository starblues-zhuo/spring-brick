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

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 插件ApplicationContext实现
 * @author starBlues
 * @version 3.0.0
 */
public class PluginApplicationContext extends AnnotationConfigApplicationContext {

    private final PluginDescriptor pluginDescriptor;

    public PluginApplicationContext(DefaultListableBeanFactory beanFactory,
                                    ProcessorContext processorContext) {
        super(beanFactory);
        setResourceLoader(processorContext.getResourceLoader());
        this.pluginDescriptor = processorContext.getPluginDescriptor();
    }

    @Override
    public void registerShutdownHook() {
        // 忽略
    }

    @Override
    public String getApplicationName() {
        return pluginDescriptor.getPluginId();
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        super.refresh();
    }

    @Override
    public void close() {
        super.close();
    }
}
