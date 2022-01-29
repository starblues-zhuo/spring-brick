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

import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.bootstrap.processor.invoke.InvokeBeanFactory;
import com.gitee.starblues.bootstrap.processor.scanner.PluginClassPathBeanDefinitionScanner;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.ApplicationContextProxy;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import com.gitee.starblues.spring.invoke.SupperCache;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ScanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

/**
 * 反射调用其他插件的处理者
 * @author starBlues
 * @version 3.0.0
 */
public class InvokeOtherPluginProcessor implements SpringPluginProcessor {

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        InvokeCallerBeanDefinitionScanner scanner = new InvokeCallerBeanDefinitionScanner(context);
        scanner.doScan(ScanUtils.getScanBasePackages(context.getRunnerClass()));
    }

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        Map<String, Object> supplierBeans = applicationContext.getBeansWithAnnotation(Supplier.class);
        String pluginId = context.getPluginDescriptor().getPluginId();
        ApplicationContext applicationContextReflection = new ApplicationContextProxy(applicationContext);
        InvokeSupperCache invokeSupperCache = context.getPluginInteractive().getInvokeSupperCache();
        supplierBeans.forEach((k,v)->{
            Supplier supplier = AnnotationUtils.findAnnotation(v.getClass(), Supplier.class);
            String supperKey = k;
            if(supplier != null && !ObjectUtils.isEmpty(supplier.value())){
                supperKey = supplier.value();
            }
            invokeSupperCache.add(pluginId, new SupperCache(supperKey, k, applicationContextReflection));
        });
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.ALL;
    }


    private static class InvokeCallerBeanDefinitionScanner extends PluginClassPathBeanDefinitionScanner {

        private final ProcessorContext context;

        public InvokeCallerBeanDefinitionScanner(ProcessorContext context) {
            super(context, false);
            setResourceLoader(context.getResourceLoader());
            this.context = context;
            addIncludeFilter(new AnnotationTypeFilter(Caller.class));
            addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            });
        }


        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
            ClassLoader pluginClassLoader = context.getClassLoader();
            InvokeSupperCache invokeSupperCache = context.getPluginInteractive().getInvokeSupperCache();
            for (BeanDefinitionHolder holder : holders) {
                AbstractBeanDefinition definition = (AbstractBeanDefinition) holder.getBeanDefinition();
                try {
                    Class<?> aClass = pluginClassLoader.loadClass(definition.getBeanClassName());
                    Caller caller = AnnotationUtils.findAnnotation(aClass, Caller.class);
                    if(caller == null){
                        continue;
                    }
                    // 是调用方
                    definition.getPropertyValues().add("callerAnnotation", caller);
                    definition.getPropertyValues().add("callerInterface", aClass);
                    definition.getPropertyValues().add("invokeSupperCache", invokeSupperCache);
                    definition.setBeanClass(InvokeBeanFactory.class);
                    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            return holders;
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }

    }


}
