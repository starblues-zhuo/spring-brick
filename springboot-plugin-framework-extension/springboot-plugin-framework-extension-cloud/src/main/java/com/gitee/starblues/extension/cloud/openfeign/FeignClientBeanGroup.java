package com.gitee.starblues.extension.cloud.openfeign;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * FeignClient bean class 分组
 * @author starBlues
 * @version 2.4.6
 */
public class FeignClientBeanGroup implements PluginClassGroupExtend {

    public static final String KEY = "feignClientBeanClass";
    public static final String GROUP_ID = "feignClientBeanClassGroupExtend";

    @Override
    public String key() {
        return KEY ;
    }

    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        if(aClass == null){
            return false;
        }
        return AnnotationsUtils.haveAnnotations(aClass, false, FeignClient.class);
    }
}
