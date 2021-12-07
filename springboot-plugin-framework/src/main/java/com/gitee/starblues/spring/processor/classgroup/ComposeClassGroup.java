package com.gitee.starblues.spring.processor.classgroup;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * @author starBlues
 * @version 1.0
 */
public class ComposeClassGroup extends PluginClassGroup{

    public final static String ID = getFullId("ComposeClassGroup");

    @Override
    public String groupId() {
        return ID;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationUtils.findAnnotation(aClass, Component.class) != null;
    }

}
