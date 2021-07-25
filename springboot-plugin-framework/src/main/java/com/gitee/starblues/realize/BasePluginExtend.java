package com.gitee.starblues.realize;

/**
 * 扩展的BasePlugin信息
 *
 * @author starBlues
 * @version 2.4.0
 */
public final class BasePluginExtend {

    private final BasePlugin basePlugin;
    private Long startTimestamp;
    private Long stopTimestamp;

    BasePluginExtend(BasePlugin basePlugin){
        this.basePlugin = basePlugin;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public Long getStopTimestamp() {
        return stopTimestamp;
    }

    void startEvent(){
        startTimestamp = System.currentTimeMillis();
    }

    void deleteEvent(){
        stopTimestamp = System.currentTimeMillis();
    }

    void stopEvent(){
        stopTimestamp = System.currentTimeMillis();
    }


}
