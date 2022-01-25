package com.gitee.starblues.integration.listener;

import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.ApplicationContext;

/**
 * 默认的初始化监听者。内置注册
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultInitializerListener implements PluginInitializerListener{

    private final SwaggerListener swaggerListener;

    public DefaultInitializerListener(ApplicationContext applicationContext) {
        this.swaggerListener = SpringBeanUtils.getExistBean(applicationContext, SwaggerListener.class);
    }


    @Override
    public void before() {

    }

    @Override
    public void complete() {
        if(swaggerListener != null){
            swaggerListener.refresh();
        }
    }

    @Override
    public void failure(Throwable throwable) {

    }
}
