package com.basic.example.plugin1.config;

import com.gitee.starblues.factory.process.pipe.PluginAutoConfigurationInstaller;
import com.gitee.starblues.realize.AutoConfigurationSelector;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

/**
 * @Description TODO
 * @Author rockstal
 * @Date 2021/4/28 21:21
 **/
public class AutoConfig implements AutoConfigurationSelector {
    @Override
    public void select(PluginAutoConfigurationInstaller installer) {
        installer.install(QuartzAutoConfiguration.class);
    }
}
