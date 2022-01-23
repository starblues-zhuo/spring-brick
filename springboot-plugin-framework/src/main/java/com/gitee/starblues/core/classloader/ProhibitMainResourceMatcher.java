package com.gitee.starblues.core.classloader;

/**
 * 禁止匹配所有主程序资源
 * @author starBlues
 * @version 3.0.0
 */
public class ProhibitMainResourceMatcher implements MainResourceMatcher{

    @Override
    public boolean match(String resourceUrl) {
        return false;
    }

}
