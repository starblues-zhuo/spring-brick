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

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.spring.extract.DefaultExtractFactory;
import com.gitee.starblues.spring.extract.DefaultOpExtractFactory;
import com.gitee.starblues.spring.extract.ExtractFactory;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 默认的插件 PluginApplication
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginApplication extends AbstractPluginApplication {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPluginApplication.class);

    private PluginUser pluginUser;
    private PluginOperator pluginOperator;

    private final AtomicBoolean beInitialized = new AtomicBoolean(false);

    public DefaultPluginApplication() {
    }

    @Override
    public synchronized void initialize(ApplicationContext applicationContext,
                                        PluginInitializerListener listener) {
        Objects.requireNonNull(applicationContext, "ApplicationContext can't be null");
        if(beInitialized.get()){
            throw new RuntimeException("Plugin has been initialized");
        }
        // 获取Configuration
        IntegrationConfiguration configuration = getConfiguration(applicationContext);
        // 检查配置
        configuration.checkConfig();
        createPluginUser(applicationContext);
        createPluginOperator(applicationContext);
        try {
            if(!(pluginOperator instanceof PluginOperatorWrapper)){
                pluginOperator = new PluginOperatorWrapper(pluginOperator, configuration);
            }
            GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
            setBeanFactory(genericApplicationContext);
            initExtractFactory(genericApplicationContext);
            pluginOperator.initPlugins(listener);
            beInitialized.set(true);
        } catch (Exception e) {
            LOG.error("初始化插件异常." + e.getMessage());
        }
    }

    /**
     * 创建插件使用者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     */
    protected synchronized PluginUser createPluginUser(ApplicationContext applicationContext){
        if(pluginUser == null){
            pluginUser = applicationContext.getBean(PluginUser.class);
        }
        return pluginUser;
    }

    /**
     * 创建插件操作者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     */
    protected synchronized PluginOperator createPluginOperator(ApplicationContext applicationContext){
        if(pluginOperator == null){
            pluginOperator = applicationContext.getBean(PluginOperator.class);
        }
        return  pluginOperator;
    }

    @Override
    public PluginOperator getPluginOperator() {
        assertInjected();
        return pluginOperator;
    }

    @Override
    public PluginUser getPluginUser() {
        assertInjected();
        return pluginUser;
    }

    /**
     * 初始化扩展工厂
     * @param applicationContext applicationContext
     */
    private void initExtractFactory(GenericApplicationContext applicationContext){
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        DefaultExtractFactory defaultExtractFactory = (DefaultExtractFactory)ExtractFactory.getInstant();
        initMainExtract((OpExtractFactory)defaultExtractFactory.getTarget(), beanFactory);
    }

    /**
     * 初始化主程序中的扩展
     * @param opExtractFactory opExtractFactory
     * @param beanFactory beanFactory
     */
    private void initMainExtract(OpExtractFactory opExtractFactory, ListableBeanFactory beanFactory){
        // 获取主程序的扩展
        Map<String, Object> extractMap = beanFactory.getBeansWithAnnotation(Extract.class);
        if(ObjectUtils.isEmpty(extractMap)){
            return;
        }
        for (Object extract : extractMap.values()) {
            opExtractFactory.addOfMain(extract);
        }
    }

    /**
     * 直接将 PluginOperator 和 PluginUser 注入到ApplicationContext容器中
     * @param applicationContext ApplicationContext
     */
    @Deprecated
    private void setBeanFactory(GenericApplicationContext applicationContext){
        DefaultListableBeanFactory defaultListableBeanFactory = applicationContext.getDefaultListableBeanFactory();
        defaultListableBeanFactory.registerSingleton(pluginOperator.getClass().getName(), pluginOperator);
        defaultListableBeanFactory.registerSingleton(pluginUser.getClass().getName(), pluginUser);
    }

    /**
     * 检查注入
     */
    private void assertInjected() {
        if (this.pluginUser == null) {
            throw new RuntimeException("PluginUser is null, Please check whether the DefaultPluginApplication is injected");
        }
        if (this.pluginOperator == null) {
            throw new RuntimeException("PluginOperator is null, Please check whether the DefaultPluginApplication is injected");
        }
    }


}
