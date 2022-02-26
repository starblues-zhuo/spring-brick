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
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.web.thymeleaf.PluginThymeleafProcessor;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.bootstrap.realize.StopValidator;
import com.gitee.starblues.bootstrap.utils.DestroyUtils;
import com.gitee.starblues.bootstrap.utils.SpringBeanUtils;
import com.gitee.starblues.core.exception.PluginProhibitStopException;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.ApplicationContextProxy;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.WebConfig;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;
import java.util.Map;

/**
 * 默认的插件钩子器
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultSpringPluginHook implements SpringPluginHook {

    private final SpringPluginProcessor pluginProcessor;
    private final ProcessorContext processorContext;
    private final StopValidator stopValidator;

    public DefaultSpringPluginHook(SpringPluginProcessor pluginProcessor,
                                   ProcessorContext processorContext) {
        this.pluginProcessor = pluginProcessor;
        this.processorContext = processorContext;
        this.stopValidator = SpringBeanUtils.getExistBean(processorContext.getApplicationContext(),
                StopValidator.class);
    }

    /**
     * 先校验是否可卸载
     */
    @Override
    public void stopVerify() {
        if(stopValidator == null){
            return;
        }
        try {
            StopValidator.Result result = stopValidator.verify();
            if(result != null && !result.isVerify()){
                throw new PluginProhibitStopException(processorContext.getPluginDescriptor(),
                        result.getMessage());
            }
        } catch (Exception e){
            throw new PluginProhibitStopException(processorContext.getPluginDescriptor(),
                    e.getMessage());
        }
    }


    @Override
    public void close() throws Exception{
        try {
            GenericApplicationContext applicationContext = processorContext.getApplicationContext();
            callPluginCloseListener(applicationContext);
            pluginProcessor.close(processorContext);
            if(applicationContext != null){
                applicationContext.close();
            }
            processorContext.clearRegistryInfo();
            DestroyUtils.destroyAll(null, SpringFactoriesLoader.class, "cache", Map.class);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return new ApplicationContextProxy(processorContext.getApplicationContext().getBeanFactory());
    }

    @Override
    public WebConfig getWebConfig() {
        return processorContext.getWebConfig();
    }

    @Override
    public ThymeleafConfig getThymeleafConfig() {
        return processorContext.getRegistryInfo(PluginThymeleafProcessor.CONFIG_KEY);
    }

    private void callPluginCloseListener(GenericApplicationContext applicationContext){
        List<PluginCloseListener> pluginCloseListeners = SpringBeanUtils.getBeans(
                applicationContext, PluginCloseListener.class);
        if(pluginCloseListeners.isEmpty()){
            return;
        }
        for (PluginCloseListener pluginCloseListener : pluginCloseListeners) {
            try {
                pluginCloseListener.close(processorContext.getPluginDescriptor());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
