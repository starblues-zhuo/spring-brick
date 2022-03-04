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

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorException;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

/**
 * Extract 扩展Bean注册处理者
 * @author starBlues
 * @version 3.0.0
 */
public class ExtractBeanProcessor implements SpringPluginProcessor {

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        Map<String, Object> extractMap = applicationContext.getBeansWithAnnotation(Extract.class);
        if(ObjectUtils.isEmpty(extractMap)){
            return;
        }
        String pluginId = context.getPluginDescriptor().getPluginId();
        OpExtractFactory opExtractFactory = context.getPluginInteractive().getOpExtractFactory();
        for (Object extract : extractMap.values()) {
            opExtractFactory.add(pluginId, extract);
        }
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        OpExtractFactory opExtractFactory = context.getPluginInteractive().getOpExtractFactory();
        String pluginId = context.getPluginDescriptor().getPluginId();
        opExtractFactory.remove(pluginId);
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.ALL;
    }
}
