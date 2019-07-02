package com.gitee.starblues.realize;

import com.gitee.starblues.loader.PluginResourceLoadFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

/**
 * 插件包要继承的抽象类
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class BasePlugin extends Plugin {

    private final PluginResourceLoadFactory pluginResourceLoadFactory;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.pluginResourceLoadFactory = new PluginResourceLoadFactory();
    }


    @Override
    public final void start() throws PluginException {
        pluginResourceLoadFactory.load(this);
    }


    @Override
    public final void delete() throws PluginException {
        deleteEvent();
    }

    @Override
    public final void stop() {
        stopEvent();
    }


    /**
     * 启动事件
     * @throws PluginException PluginException
     */
    protected abstract void startEvent() throws PluginException;

    /**
     * 删除事件
     * @throws PluginException PluginException
     */
    protected abstract void deleteEvent() throws PluginException;

    /**
     * 停止事件
     */
    protected abstract void stopEvent();

    /**
     * 扫描包。默认为当前类包名。可重写自定义包名
     * @return 包名
     */
    public String scanPackage(){
        return this.getCurrentPackageName();
    }


    /**
     * 插件资源加载者。
     * @return PluginResourceLoadFactory
     */
    public final PluginResourceLoadFactory getPluginResourceLoadFactory() {
        return pluginResourceLoadFactory;
    }

    /**
     * 获取当前实现类的包名
     * @return 包名
     */
    private String getCurrentPackageName(){
        return this.getClass().getPackage().getName();
    }

}
