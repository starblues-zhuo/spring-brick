package com.plugin.development.realize;

import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Objects;

/**
 * 插件包要继承的抽象类
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class BasePlugin extends Plugin implements PluginApplicationContext{

    private volatile AnnotationConfigApplicationContext applicationContext;


    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public final AnnotationConfigApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = createApplicationContext();
        }
        return applicationContext;
    }

    @Override
    public void start() throws PluginException {
        getApplicationContext();
    }

    @Override
    public void delete() throws PluginException {
        stop();
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
            applicationContext = null;
        }
    }



    /**
     * 创建当前插件包的 ApplicationContext
     * @return 注解上下文
     */
    private AnnotationConfigApplicationContext createApplicationContext(){
        String scanPackage = scanPackage();
        if(scanPackage == null || Objects.equals("", scanPackage)){
            scanPackage = getCurrentPackageName();
        }
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.setAllowCircularReferences(false);
        applicationContext.scan(scanPackage);
        return applicationContext;
    }


    /**
     * 获取当前实现类的包名
     * @return 包名
     */
    private String getCurrentPackageName(){
        return this.getClass().getPackage().getName();
    }

    /**
     * 扫描包。默认为当前类包名。可重写自定义包名
     * @return 包名
     */
    protected String scanPackage(){
        return this.getCurrentPackageName();
    }






}
