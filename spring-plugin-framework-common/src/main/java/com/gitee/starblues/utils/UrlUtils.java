/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.utils;

/**
 * http url util
 *
 * @author starBlues
 * @version 3.0.0
 */
public class UrlUtils {

    private UrlUtils(){}

    public static final String SPLIT = "/";

    /**
     * rest接口拼接路径
     *
     * @param path1 路径1
     * @param path2 路径2
     * @return 拼接的路径
     */
    public static String restJoiningPath(String path1, String path2){
        if(path1 != null && path2 != null){
            if(path1.endsWith(SPLIT) && path2.startsWith(SPLIT)){
                return path1 + path2.substring(1);
            } else if(!path1.endsWith(SPLIT) && !path2.startsWith(SPLIT)){
                return path1 + SPLIT + path2;
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
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningUrlPath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(ObjectUtils.isEmpty(path)) {
                continue;
            }
            if((i < length - 1) && path.endsWith(SPLIT)){
                path = path.substring(path.lastIndexOf(SPLIT));
            }
            if(path.startsWith(SPLIT)){
                stringBuilder.append(path);
            } else {
                stringBuilder.append(SPLIT).append(path);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 格式化 url
     * @param url 原始url
     * @return 格式化后的url
     */
    public static String format(String url){
        if(ObjectUtils.isEmpty(url)){
            return url;
        }
        String[] split = url.split(SPLIT);
        StringBuilder stringBuilder = new StringBuilder();
        int length = split.length;
        for (int i = 0; i < length; i++) {
            String str = split[i];
            if(ObjectUtils.isEmpty(str)){
                continue;
            }
            if(i < length - 1){
                stringBuilder.append(str).append(SPLIT);
            } else {
                stringBuilder.append(str);
            }
        }
        return stringBuilder.toString();
    }

}
