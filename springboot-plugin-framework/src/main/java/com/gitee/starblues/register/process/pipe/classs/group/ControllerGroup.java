package com.gitee.starblues.register.process.pipe.classs.group;

import com.gitee.starblues.register.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分组存在注解: @Controller、@RestController
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ControllerGroup implements PluginClassGroup {


    /**
     * spring @Controller @RestController 注解bean
     */
    public static final String SPRING_CONTROLLER= "spring_controller";


    @Override
    public String groupId() {
        return SPRING_CONTROLLER;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, RestController.class, Controller.class);
    }
}
