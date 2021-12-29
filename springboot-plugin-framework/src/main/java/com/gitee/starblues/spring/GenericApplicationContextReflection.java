package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
public class GenericApplicationContextReflection implements ApplicationContext {

    protected final Object genericApplicationContext;

    public GenericApplicationContextReflection(Object genericApplicationContext) {
        this.genericApplicationContext = genericApplicationContext;
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
        prohibitCall("getBeanNamesForType");
        return null;
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

    protected <T> T invokeReturn(String methodName, Object... params){
        return invoke(true, methodName, params);
    }

    protected void invokeNoReturn(String methodName, Object... params){
        invoke(false, methodName, params);
    }

    protected <T> T invoke(boolean haveReturn, String methodName, Object... params){
        return ReflectionUtils.invoke(genericApplicationContext, methodName, params);
    }

    private void prohibitCall(String name){
        throw new IllegalStateException("Prohibit call: " + name);
    }

}
