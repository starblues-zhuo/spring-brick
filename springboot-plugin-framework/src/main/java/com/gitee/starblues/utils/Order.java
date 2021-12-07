package com.gitee.starblues.utils;

/**
 * @author starBlues
 * @version 1.0
 */
public interface Order {

    /**
     * 排序, 数字越大越先执行
     * @return OrderPriority
     */
    OrderPriority order();


}
