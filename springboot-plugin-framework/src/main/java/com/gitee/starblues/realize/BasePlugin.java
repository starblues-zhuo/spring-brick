package com.gitee.starblues.realize;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * 插件包要继承的抽象类。
 * 注意: 实现该类的子类无法使用依赖注入
 * @author zhangzhuo
 * @version 2.2.0
 */
public abstract class BasePlugin extends Plugin {

    private final BasePluginExtend basePluginExtend;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.basePluginExtend = new BasePluginExtend(this);
    }


    @Override
    public final void start() {
        try {
            startEvent();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            basePluginExtend.startEvent();
        }
    }


    @Override
    public final void delete() {
        try {
            deleteEvent();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            basePluginExtend.deleteEvent();
        }

    }

    @Override
    public final void stop() {
        try {
            stopEvent();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            basePluginExtend.startEvent();
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
     * 得到插件扩展的信息
     * @return BasePluginExtend
     */
    public final BasePluginExtend getBasePluginExtend() {
        return basePluginExtend;
    }

    /**
     * 启动事件. Spring 容器都没有准备。无法使用注入。
     */
    protected void startEvent(){

    }

    /**
     * 删除事件. 在插件删除时触发。
     */
    protected void deleteEvent(){

    }

    /**
     * 停止事件. 在插件停止时触发。
     */
    protected void stopEvent(){

    }

}
