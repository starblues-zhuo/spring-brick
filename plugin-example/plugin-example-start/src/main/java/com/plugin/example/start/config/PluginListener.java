package com.plugin.example.start.config;

import com.plugin.development.integration.initialize.PluginInitializerListener;
import org.springframework.stereotype.Component;

/**
 * 插件监听者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class PluginListener implements PluginInitializerListener {
    @Override
    public void before() {
        System.out.println("before");
    }

    @Override
    public void complete() {
        System.out.println("complete");
    }

    @Override
    public void failure(Throwable throwable) {
        System.out.println("failure:"+throwable.getMessage());
    }
}
