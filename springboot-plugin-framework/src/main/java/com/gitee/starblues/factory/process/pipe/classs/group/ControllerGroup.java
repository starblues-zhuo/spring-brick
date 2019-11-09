package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分组存在注解: @Controller、@RestController
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class ControllerGroup implements PluginClassGroup {


    /**
     * spring @Controller @RestController 注解bean
     */
    public static final String GROUP_ID= "spring_controller";


    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, RestController.class, Controller.class);
    }
}
