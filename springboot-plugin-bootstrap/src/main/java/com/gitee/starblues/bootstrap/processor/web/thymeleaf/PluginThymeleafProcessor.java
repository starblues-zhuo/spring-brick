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

package com.gitee.starblues.bootstrap.processor.web.thymeleaf;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorException;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 插件 Thymeleaf 注册
 * @author starBlues
 * @version 3.0.0
 */
public class PluginThymeleafProcessor implements SpringPluginProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PluginThymeleafProcessor.class);

    public static final String CONFIG_KEY = "ThymeleafConfig";


    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        ThymeleafConfig thymeleafConfig = ThymeleafConfigParse.parse(applicationContext.getEnvironment());
        context.addRegistryInfo(CONFIG_KEY, thymeleafConfig);
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        context.removeRegistryInfo(CONFIG_KEY);
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }


}
