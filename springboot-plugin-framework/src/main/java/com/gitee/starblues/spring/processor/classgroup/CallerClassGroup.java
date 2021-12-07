package com.gitee.starblues.spring.processor.classgroup;

import com.gitee.starblues.annotation.Caller;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author starBlues
 * @version 1.0
 */
public class CallerClassGroup extends PluginClassGroup{

    public final static String ID = getFullId("CallerClassGroup");

    @Override
    public String groupId() {
        return ID;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationUtils.findAnnotation(aClass, Caller.class) != null;
    }

}
