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

package com.gitee.starblues.bootstrap.processor.scanner;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;


/**
 * 插件自定义 classpath bean 扫描
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public PluginClassPathBeanDefinitionScanner(ProcessorContext processorContext) {
        this(processorContext, true);
    }

    public PluginClassPathBeanDefinitionScanner(ProcessorContext processorContext, boolean useDefaultFilters) {
        super(processorContext.getApplicationContext(), useDefaultFilters,
                processorContext.getApplicationContext().getEnvironment(),
                processorContext.getResourceLoader());
    }

}
