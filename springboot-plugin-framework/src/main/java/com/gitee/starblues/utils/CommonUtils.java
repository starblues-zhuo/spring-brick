package com.gitee.starblues.utils;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.util.StringUtils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 通用工具
 *
 * @author starBlues
 * @version 2.2.1
 */
public class CommonUtils {

    private CommonUtils(){}

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


    /**
     * 得到插件接口前缀
     * @param configuration 配置
     * @param pluginId 插件id
     * @return 接口前缀
     */
    public static String getPluginRestPrefix(IntegrationConfiguration configuration, String pluginId){
        String pathPrefix = configuration.pluginRestPathPrefix();
        if(configuration.enablePluginIdRestPathPrefix()){
            if(pathPrefix != null && !"".equals(pathPrefix)){
                pathPrefix = restJoiningPath(pathPrefix, pluginId);
            } else {
                pathPrefix = pluginId;
            }
            return pathPrefix;
        } else {
            if(pathPrefix == null || "".equals(pathPrefix)){
                // 不启用插件id作为路径前缀, 并且路径前缀为空, 则直接返回。
                return null;
            }
        }
        return pathPrefix;
    }


    /**
     * rest接口拼接路径
     * @param path1 路径1
     * @param path2 路径2
     * @return 拼接的路径
     */
    public static String restJoiningPath(String path1, String path2){
        if(path1 != null && path2 != null){
            if(path1.endsWith("/") && path2.startsWith("/")){
                return path1 + path2.substring(1);
            } else if(!path1.endsWith("/") && !path2.startsWith("/")){
                return path1 + "/" + path2;
            } else {
                return path1 + path2;
            }
        } else if(path1 != null){
            return path1;
        } else if(path2 != null){
            return path2;
        } else {
            return "";
        }
    }


    /**
     * 拼接url路径
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningPath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(StringUtils.isNullOrEmpty(path)) {
                continue;
            }
            if((i < length - 1) && path.endsWith("/")){
                path = path.substring(path.lastIndexOf("/"));
            }
            if(path.startsWith("/")){
                stringBuilder.append(path);
            } else {
                stringBuilder.append("/").append(path);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 拼接file路径
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningFilePath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(StringUtils.isNullOrEmpty(path)) {
                continue;
            }
            if(i > 0){
                if(path.startsWith(File.separator) || path.startsWith("/") ||
                        path.startsWith("\\") || path.startsWith("//")){
                    stringBuilder.append(path);
                } else {
                    stringBuilder.append(File.separator).append(path);
                }
            } else {
                stringBuilder.append(path);
            }
        }

        return stringBuilder.toString();
    }


}
