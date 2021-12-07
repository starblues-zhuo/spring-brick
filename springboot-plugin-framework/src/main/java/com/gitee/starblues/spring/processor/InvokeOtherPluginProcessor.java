package com.gitee.starblues.spring.processor;

import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.classgroup.CallerClassGroup;
import com.gitee.starblues.spring.processor.invoke.InvokeBeanFactory;
import com.gitee.starblues.spring.processor.invoke.InvokeSupperCache;
import com.gitee.starblues.spring.processor.scanner.PluginClassPathBeanDefinitionScanner;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ScanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class InvokeOtherPluginProcessor implements SpringPluginProcessor{


    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        InvokeCallerBeanDefinitionScanner scanner = new InvokeCallerBeanDefinitionScanner(registryInfo);
        scanner.doScan(ScanUtils.getScanBasePackages(registryInfo.getPluginWrapper().getPluginClass()));
        // 注册发现 supper bean 的后置处理器
        applicationContext.registerBean(FindSupperBeanPostProcessor.class, registryInfo);
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }


    private static class InvokeCallerBeanDefinitionScanner extends PluginClassPathBeanDefinitionScanner {

        private final SpringPluginRegistryInfo registryInfo;

        public InvokeCallerBeanDefinitionScanner(SpringPluginRegistryInfo registryInfo) {
            super(registryInfo, false);
            setResourceLoader(new DefaultResourceLoader(registryInfo.getPluginWrapper().getPluginClassLoader()));
            this.registryInfo = registryInfo;
            addIncludeFilter(new AnnotationTypeFilter(Caller.class));
            addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            });
        }


        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
            ClassLoader pluginClassLoader = registryInfo.getPluginWrapper().getPluginClassLoader();
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
                    definition.getPropertyValues().add("registryInfo", registryInfo);
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


    private static class FindSupperBeanPostProcessor implements BeanPostProcessor {

        private final SpringPluginRegistryInfo registryInfo;

        private FindSupperBeanPostProcessor(SpringPluginRegistryInfo registryInfo) {
            this.registryInfo = registryInfo;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Supplier supplier = AnnotationUtils.findAnnotation(bean.getClass(), Supplier.class);
            if(supplier != null){
                String pluginId = registryInfo.getPluginWrapper().getPluginId();
                GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                        .getApplicationContext();
                InvokeSupperCache.add(pluginId, new InvokeSupperCache.Cache(supplier.value(), beanName,
                        applicationContext));
            }
            return bean;
        }
    }

}
