package com.gitee.starblues.utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 操作插件信息。为了解决连续上传安装后, 停止后, 无法启动的问题。
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class PluginOperatorInfo {


    /**
     * 当前操作类型
     */
    private OperatorType operatorType;

    /**
     * 是否锁定。如果锁定, 则不允许更新操作类型。
     * 该参数主要解决在安装插件时 setOperatorType 一次操作类型，但是它又调用了启动插件，而启动插件也要 setOperatorType 一次操作类型
     *   所以, 使用该参数, 用于在安装插件时, 锁定 setOperatorType, 在启动时就无法再 setOperatorType 了, 就不会覆盖该值
     */
    private AtomicBoolean isLock = new AtomicBoolean(false);

    /**
     * 设置操作类型
     * @param operatorType 操作类型
     */
    public void setOperatorType(OperatorType operatorType) {
        if(operatorType != null && !isLock.get()){
            // 如果锁定了, 则不能更新操作类型
            this.operatorType = operatorType;
        }
    }

    /**
     * 主要用于锁定或者解锁。
     * @param isLock 是否锁定。true 锁定, false 解锁
     */
    public void setLock(boolean isLock){
        this.isLock.set(isLock);
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    /**
     * 操作类型
     */
    public enum OperatorType{
        /**
         * 启动插件
         */
        START,

        /**
         * 安装插件
         */
        INSTALL,
    }


}
