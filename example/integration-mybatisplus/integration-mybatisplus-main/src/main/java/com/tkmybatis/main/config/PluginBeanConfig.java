package com.tkmybatis.main.config;

import com.gitee.starblues.extension.mybatis.SpringBootMybatisExtension;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 插件集成配置
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 15:53
 * @Update Date Time:
 * @see
 */
@Configuration
public class PluginBeanConfig {


    /**
     * 定义插件应用。使用可以注入它操作插件。
     * @return PluginApplication
     */
    @Bean
    public PluginApplication pluginApplication(){
        // 实例化自动初始化插件的PluginApplication
        PluginApplication pluginApplication = new AutoPluginApplication();
        pluginApplication.addExtension(new SpringBootMybatisExtension(
                SpringBootMybatisExtension.Type.MYBATIS_PLUS
        ));
        return pluginApplication;
    }

}
