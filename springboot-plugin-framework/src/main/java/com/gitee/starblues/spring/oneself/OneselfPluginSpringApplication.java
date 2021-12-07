package com.gitee.starblues.spring.oneself;


import org.springframework.context.support.GenericApplicationContext;

/**
 * 插件可主运行的 PluginSpringApplication 实现, 插件如果需要自动运行, 则使用该类进行引导启动
 * @author starBlues
 * @version 3.0.0
 */
public class OneselfPluginSpringApplication {

    public static GenericApplicationContext run(Class<?> ...primarySources){
        try {
            return new OneselfSpringApplication(primarySources).run();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
