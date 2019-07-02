package com.gitee.starblues.utils;

/**
 * 执行顺序
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class OrderExecution {

    private OrderExecution(){}

    /**
     * 低优先级
     */
    public static final int LOW = Integer.MAX_VALUE;


    /**
     * 中优先级
     */
    public static final int MIDDLE = Integer.MAX_VALUE;



    /**
     * 高优先级
     */
    public static final int HIGH = Integer.MIN_VALUE;



}
