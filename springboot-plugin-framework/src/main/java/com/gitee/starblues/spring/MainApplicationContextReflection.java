package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class MainApplicationContextReflection extends GenericApplicationContextReflection implements MainApplicationContext {

    private ClassLoader mainClassLoader;

    public MainApplicationContextReflection(Object mainGenericApplicationContext) {
        super(mainGenericApplicationContext);
        this.mainClassLoader = mainGenericApplicationContext.getClass().getClassLoader();
    }

    public void setMainClassLoader(ClassLoader mainClassLoader) {
        this.mainClassLoader = mainClassLoader;
    }

    @Override
    public Object resolveDependency(Object descriptor, String requestingBeanName) {

        return null;
    }

    @Override
    public Object resolveDependency(Object descriptor, String requestingBeanName,
                                    Set<String> autowiredBeanNames, Object typeConverter)  {
        try {
            Class<?> descriptorClass = mainClassLoader.loadClass(descriptor.getClass().getName());
            Class<?> typeConverterClass = mainClassLoader.loadClass(typeConverter.getClass().getName());

            Object descriptorObject = descriptorClass.getConstructor(Field.class, boolean.class).newInstance(
                    ReflectionUtils.getField(descriptor, "field", Field.class),
                    ReflectionUtils.getField(descriptor, "required", boolean.class)
            );

            Object o = invokeReturn("getBeanFactory");
            return ReflectionUtils.invoke(o, "resolveDependency", descriptorObject, requestingBeanName,
                    autowiredBeanNames, typeConverterClass.getConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
