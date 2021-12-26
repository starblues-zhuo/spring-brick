package com.gitee.starblues.spring;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author starBlues
 * @version 1.0
 */
public class GenericApplicationContextReflection implements ApplicationContext {

    private final Object genericApplicationContext;
    private final Class<?> mainApplicationContextClass;

    public GenericApplicationContextReflection(Object genericApplicationContext) {
        this.genericApplicationContext = genericApplicationContext;
        this.mainApplicationContextClass = genericApplicationContext.getClass();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return invokeReturn("containsBeanDefinition", beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return invokeReturn("getBeanDefinitionCount");
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return invokeReturn("getBeanDefinitionNames");
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return invokeReturn("getBeanNamesForType", type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return invokeReturn("getBeanNamesForType", type);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return invokeReturn("getBeanNamesForType", includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return invokeReturn("getBeansOfType", type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return invokeReturn("getBeansOfType", type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return invokeReturn("getBeanNamesForAnnotation", annotationType);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        return invokeReturn("getBeansWithAnnotation", annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return invokeReturn("findAnnotationOnBean", beanName, annotationType);
    }

    @Override
    public <T> T createBean(Class<T> beanClass) throws BeansException {
        prohibitCall("createBean");
        return null;
    }

    @Override
    public void autowireBean(Object existingBean) throws BeansException {
        prohibitCall("autowireBean");
    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        prohibitCall("configureBean");
        return null;
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        prohibitCall("createBean");
        return null;
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        prohibitCall("autowire");
        return null;
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        prohibitCall("autowireBeanProperties");
    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        prohibitCall("applyBeanPropertyValues");
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) throws BeansException {
        prohibitCall("initializeBean");
        return null;
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        prohibitCall("applyBeanPostProcessorsBeforeInitialization");
        return null;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        prohibitCall("applyBeanPostProcessorsAfterInitialization");
        return null;
    }

    @Override
    public void destroyBean(Object existingBean) {
        prohibitCall("destroyBean");
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        prohibitCall("resolveNamedBean");
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        return null;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return invokeReturn("getBean", name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return invokeReturn("getBean", name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return invokeReturn("getBean", name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return invokeReturn("getBean", requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return invokeReturn("getBean", requiredType, args);
    }

    @Override
    public boolean containsBean(String name) {
        return invokeReturn("containsBean", name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return invokeReturn("isSingleton", name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return invokeReturn("isPrototype", name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return invokeReturn("isTypeMatch", name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return invokeReturn("isTypeMatch", name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return invokeReturn("getType", name);
    }

    @Override
    public String[] getAliases(String name) {
        return invokeReturn("getAliases", name);
    }


    private <T> T invokeReturn(String methodName, Object... params){
        return invoke(true, methodName, params);
    }

    private void invokeNoReturn(String methodName, Object... params){
        invoke(false, methodName, params);
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(boolean haveReturn, String methodName, Object... params){
        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }
        Method method = ReflectionUtils.findMethod(mainApplicationContextClass, methodName, paramTypes);
        if(method == null){
            throw new RuntimeException("Not found method : " + methodToString(methodName, paramTypes));
        }
        try {
            if(haveReturn){
                return (T) method.invoke(genericApplicationContext, params);
            } else {
                method.invoke(genericApplicationContext, params);
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot call method : " + methodToString(methodName, paramTypes));
        }
    }

    private String methodToString(String name, Class<?>[] argTypes) {
        StringJoiner sj = new StringJoiner(", ", getName() + "." + name + "(", ")");
        if (argTypes != null) {
            for (Class<?> c : argTypes) {
                sj.add((c == null) ? "null" : c.getName());
            }
        }
        return sj.toString();
    }

    private String getName() {
        return mainApplicationContextClass.getName();
    }

    private void prohibitCall(String name){
        throw new IllegalStateException("Prohibit call: " + name);
    }

}
