package com.gitee.starblues.realize;

import com.gitee.starblues.loader.PluginResourceLoadFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * 插件包要继承的抽象类
 * @author zhangzhuo
 * @version 2.2.0
 */
public abstract class BasePlugin extends Plugin {

    private final PluginResourceLoadFactory pluginResourceLoadFactory;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.pluginResourceLoadFactory = new PluginResourceLoadFactory();
    }


    @Override
    public final void start() {
        try {
            startEvent();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pluginResourceLoadFactory.load(this);
        }
    }


    @Override
    public final void delete() {
        try {
            deleteEvent();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            pluginResourceLoadFactory.unload(this);
        }

    }

    @Override
    public final void stop() {
        try {
            stopEvent();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            pluginResourceLoadFactory.unload(this);
        }
    }

    /**
     * 扫描包。默认为当前类包名。可重写自定义包名
     * @return 包名
     */
    public String scanPackage(){
        // 获取当前实现类的包名
        return this.getClass().getPackage().getName();
    }


    /**
     * 插件资源加载者。
     * @return PluginResourceLoadFactory
     */
    public final PluginResourceLoadFactory getPluginResourceLoadFactory() {
        return pluginResourceLoadFactory;
    }


    /**
     * 启动事件. Spring 容器都没有准备。无法使用注入。
     */
    protected abstract void startEvent();

    /**
     * 删除事件. 在插件删除时触发。
     */
    protected abstract void deleteEvent();

    /**
     * 停止事件. 在插件停止时触发。
     */
    protected abstract void stopEvent();

}
