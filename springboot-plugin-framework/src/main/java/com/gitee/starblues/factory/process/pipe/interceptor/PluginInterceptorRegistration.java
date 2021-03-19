package com.gitee.starblues.factory.process.pipe.interceptor;

import com.gitee.starblues.utils.CommonUtils;
import org.pf4j.util.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 插件拦截器注册的信息
 * @author starBlues
 * @version 2.4.1
 */
public class PluginInterceptorRegistration {

    private final HandlerInterceptor interceptor;
    private final PluginInterceptorRegistry.Type type;
    private final String pluginRestApiPrefix;

    private final List<String> includePatterns = new ArrayList<>();

    private final List<String> excludePatterns = new ArrayList<>();

    @Nullable
    private PathMatcher pathMatcher;

    private int order = 0;

    /**
     * Create an {@link InterceptorRegistration} instance.
     *
     * @param interceptor 拦截器
     * @param type 类型
     * @param pluginRestApiPrefix 接口前缀
     */
    public PluginInterceptorRegistration(HandlerInterceptor interceptor,
                                         PluginInterceptorRegistry.Type type,
                                         String pluginRestApiPrefix) {
        this.interceptor = interceptor;
        this.type = type;
        String apiPrefix = null;
        if(pluginRestApiPrefix.startsWith("/")){
            apiPrefix = pluginRestApiPrefix;
        } else {
            apiPrefix = "/" + pluginRestApiPrefix;
        }
        if(apiPrefix.endsWith("/")){
            apiPrefix = apiPrefix.substring(0, apiPrefix.lastIndexOf("/"));
        }
        this.pluginRestApiPrefix = apiPrefix;
    }


    /**
     * Add URL patterns to which the registered interceptor should apply to.
     * @param patterns patterns
     * @return PluginInterceptorRegistration
     */
    public PluginInterceptorRegistration addPathPatterns(String... patterns) {
        if(type == PluginInterceptorRegistry.Type.GLOBAL){
            this.includePatterns.addAll(Arrays.asList(patterns));
        }
        // 局部的
        for (String pattern : patterns) {
            if(StringUtils.isNullOrEmpty(pattern)){
                continue;
            }
            this.includePatterns.add(CommonUtils.joiningPath(pluginRestApiPrefix, pattern));
        }
        return this;
    }

    /**
     * Add URL patterns to which the registered interceptor should not apply to.
     * @param patterns patterns
     * @return PluginInterceptorRegistration
     */
    public PluginInterceptorRegistration excludePathPatterns(String... patterns) {
        if(type == PluginInterceptorRegistry.Type.GLOBAL){
            this.excludePatterns.addAll(Arrays.asList(patterns));
        }
        for (String pattern : patterns) {
            if(StringUtils.isNullOrEmpty(pattern)){
                continue;
            }
            this.excludePatterns.add(CommonUtils.joiningPath(pluginRestApiPrefix, pattern));
        }
        return this;
    }


    /**
     * A PathMatcher implementation to use with this interceptor. This is an optional,
     * advanced property required only if using custom PathMatcher implementations
     * that support mapping metadata other than the Ant path patterns supported
     * by default.
     * @param pathMatcher pathMatcher
     * @return PluginInterceptorRegistration
     */
    public PluginInterceptorRegistration pathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }

    /**
     * Specify an order position to be used. Default is 0.
     * @param order order
     * @since 5.0
     * @return PluginInterceptorRegistration
     */
    public PluginInterceptorRegistration order(int order){
        this.order = order;
        return this;
    }

    /**
     * Return the order position to be used.
     * @since 5.0
     * @return int
     */
    protected int getOrder() {
        return this.order;
    }


    /**
     * Build the underlying interceptor. If URL patterns are provided, the returned
     * type is {@link MappedInterceptor}; otherwise {@link HandlerInterceptor}.
     * @return object 为 {@link MappedInterceptor} or {@link HandlerInterceptor}
     */
    protected Object getInterceptor() {
        if(type == PluginInterceptorRegistry.Type.PLUGIN){
            if(this.includePatterns.isEmpty()){
                this.includePatterns.add(CommonUtils.joiningPath(pluginRestApiPrefix, "/**"));
            }
        }
        if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
            return this.interceptor;
        }

        String[] include = this.includePatterns.toArray(new String[]{});
        String[] exclude = this.excludePatterns.toArray(new String[]{});
        MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor);
        if (this.pathMatcher != null) {
            mappedInterceptor.setPathMatcher(this.pathMatcher);
        }
        return mappedInterceptor;
    }

}
