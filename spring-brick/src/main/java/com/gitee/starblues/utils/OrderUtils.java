/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.utils;


import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 通用工具
 *
 * @author starBlues
 * @version 3.0.0
 */
public class OrderUtils {

    private OrderUtils(){}

    /**
     * list按照int排序. 数字越大, 越排在前面
     * @param list list集合
     * @param orderImpl 排序实现
     * @param <T> T
     * @return List
     */
    public static <T> List<T> order(List<T> list, Function<T, Integer> orderImpl){
        if(list == null){
            return null;
        }
        list.sort(Comparator.comparing(orderImpl, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }


    /**
     * 对 OrderPriority 进行排序操作
     * @param order OrderPriority
     * @param <T> 当前操作要被排序的bean
     * @return Comparator
     */
    public static <T> Comparator<T> orderPriority(final Function<T, OrderPriority> order){
        return Comparator.comparing(t -> {
            OrderPriority orderPriority = order.apply(t);
            if(orderPriority == null){
                orderPriority = OrderPriority.getLowPriority();
            }
            return orderPriority.getPriority();
        }, Comparator.nullsLast(Comparator.reverseOrder()));
    }


}
