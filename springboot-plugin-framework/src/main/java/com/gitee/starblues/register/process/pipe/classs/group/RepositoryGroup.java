package com.gitee.starblues.register.process.pipe.classs.group;

import com.gitee.starblues.register.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

/**
 * 分组存在注解: @Repository
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class RepositoryGroup implements PluginClassGroup {

    /**
     * spring @Repository 注解bean
     */
    public static final String SPRING_REPOSITORY= "spring_repository";


    @Override
    public String groupId() {
        return SPRING_REPOSITORY;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, Repository.class);
    }
}
