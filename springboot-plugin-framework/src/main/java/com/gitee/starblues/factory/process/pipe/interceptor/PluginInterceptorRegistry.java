package com.gitee.starblues.factory.process.pipe.interceptor;

import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件拦截器添加者
 * @author starBlues
 * @version 2.4.1
 */
public class PluginInterceptorRegistry {


    private final List<PluginInterceptorRegistration> registrations = new ArrayList<>();
    private final String pluginRestApiPrefix;

    public PluginInterceptorRegistry(String pluginRestApiPrefix) {
        this.pluginRestApiPrefix = pluginRestApiPrefix;
    }

    /**
     * Adds the provided {@link HandlerInterceptor}.
     * @param interceptor the interceptor to add
     * @param type type {@link Type}
     * @return An {@link InterceptorRegistration} that allows you optionally configure the
     * registered interceptor further for example adding URL patterns it should apply to.
     */
    public PluginInterceptorRegistration addInterceptor(HandlerInterceptor interceptor, Type type) {
        PluginInterceptorRegistration registration = new PluginInterceptorRegistration(interceptor,
                type, pluginRestApiPrefix);
        this.registrations.add(registration);
        return registration;
    }

    /**
     * Adds the provided {@link WebRequestInterceptor}.
     * @param interceptor the interceptor to add
     * @param type type {@link Type}
     *
     * @return An {@link InterceptorRegistration} that allows you optionally configure the
     * registered interceptor further for example adding URL patterns it should apply to.
     */
    public PluginInterceptorRegistration addWebRequestInterceptor(WebRequestInterceptor interceptor, Type type) {
        WebRequestHandlerInterceptorAdapter adapted = new WebRequestHandlerInterceptorAdapter(interceptor);
        PluginInterceptorRegistration registration = new PluginInterceptorRegistration(adapted, type,
                pluginRestApiPrefix);
        this.registrations.add(registration);
        return registration;
    }

    /**
     * Return all registered interceptors.
     * @return interceptors
     */
    public List<Object> getInterceptors() {
        return this.registrations.stream()
                .sorted(INTERCEPTOR_ORDER_COMPARATOR)
                .map(PluginInterceptorRegistration::getInterceptor)
                .collect(Collectors.toList());
    }


    public enum Type{
        /**
         * 全局拦截器
         */
        GLOBAL,

        /**
         * 插件局部拦截器, 必须设置 pluginRestPathPrefix 的值才生效
         */
        PLUGIN
    }


    private static final Comparator<Object> INTERCEPTOR_ORDER_COMPARATOR =
            OrderComparator.INSTANCE.withSourceProvider(object -> {
                if (object instanceof PluginInterceptorRegistration) {
                    return (Ordered) ((PluginInterceptorRegistration) object)::getOrder;
                }
                return null;
            });



}
