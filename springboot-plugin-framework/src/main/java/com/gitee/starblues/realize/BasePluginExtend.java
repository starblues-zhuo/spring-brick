package com.gitee.starblues.realize;

import com.gitee.starblues.loader.PluginResourceLoadFactory;

/**
 * 扩展的BasePlugin信息
 *
 * @author zhangzhuo
 * @version 1.0
 */
public final class BasePluginExtend {

    private final BasePlugin basePlugin;
    private final PluginResourceLoadFactory pluginResourceLoadFactory;
    private Long startTimestamp;
    private Long stopTimestamp;

    BasePluginExtend(BasePlugin basePlugin){
        this.basePlugin = basePlugin;
        this.pluginResourceLoadFactory = new PluginResourceLoadFactory();
    }


    public long getStartTimestamp() {
        return startTimestamp;
    }

    public Long getStopTimestamp() {
        return stopTimestamp;
    }

    public PluginResourceLoadFactory getPluginResourceLoadFactory() {
        return pluginResourceLoadFactory;
    }

    void startEvent(){
        try {
            pluginResourceLoadFactory.load(basePlugin);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            startTimestamp = System.currentTimeMillis();
        }
    }

    void deleteEvent(){
        try {
            pluginResourceLoadFactory.unload(basePlugin);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void stopEvent(){
        try {
            pluginResourceLoadFactory.unload(basePlugin);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            stopTimestamp = System.currentTimeMillis();
        }
    }


}
