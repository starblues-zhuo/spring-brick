package com.gitee.starblues.integration.listener;

import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.extension.ExtensionInitializer;
import org.springframework.context.ApplicationContext;

/**
 * 默认的初始化监听者。内置注册
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultInitializerListener implements PluginInitializerListener{

    public final ApplicationContext applicationContext;

    public DefaultInitializerListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void before() {
        // 初始化扩展注册信息
        ExtensionInitializer.initialize(applicationContext);
    }

    @Override
    public void complete() {

    }

    @Override
    public void failure(Throwable throwable) {

    }
}
