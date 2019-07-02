package com.gitee.starblues.utils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 通用工具
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class CommonUtils {

    private CommonUtils(){}

    /**
     * list按照int排序
     * @param list list集合
     * @param orderImpl 排序实现
     * @param <T> T
     * @return List
     */
    public static <T> List<T> order(List<T> list, Function<T, Integer> orderImpl){
        if(list == null){
            return list;
        }
        list.sort(Comparator.comparing(orderImpl, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }


}
