package com.gitee.starblues.core.classloader;

/**
 * 主程序资源匹配者
 * @author starBlues
 * @version 3.0.0
 */
public interface MainResourceMatcher {

    /**
     * 匹配主程序资源
     * @param resourceUrl 主程序资源url
     * @return true 匹配成功, false 匹配失败
     */
    boolean match(String resourceUrl);

}
