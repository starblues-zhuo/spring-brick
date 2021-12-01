package com.gitee.starblues.factory.process.post.bean.model;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

/**
 * controller 包装
 * @author starBlues
 * @version 3.0.0
 */
public class ControllerWrapper {

    /**
     * controller bean 名称
     */
    private String beanName;

    /**
     * controller 路径前缀
     */
    private String[] pathPrefix;

    /**
     * controller bean 类型
     */
    private Class<?> beanClass;

    /**
     * controller 的 RequestMappingInfo 集合
     */
    private Set<RequestMappingInfo> requestMappingInfos;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String[] getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String[] pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public Set<RequestMappingInfo> getRequestMappingInfos() {
        return requestMappingInfos;
    }

    public void setRequestMappingInfos(Set<RequestMappingInfo> requestMappingInfos) {
        this.requestMappingInfos = requestMappingInfos;
    }

}
