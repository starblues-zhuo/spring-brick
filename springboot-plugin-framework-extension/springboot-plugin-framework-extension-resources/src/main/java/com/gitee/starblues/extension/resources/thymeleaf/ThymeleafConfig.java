package com.gitee.starblues.extension.resources.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-20
 */
public class ThymeleafConfig {


    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    public static final String DEFAULT_PREFIX = "templates/";

    public static final String DEFAULT_SUFFIX = ".html";

    /**
     * Prefix that gets prepended to view names when building a URL.
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * Suffix that gets appended to view names when building a URL.
     */
    private String suffix = DEFAULT_SUFFIX;

    /**
     * Template mode to be applied to templates. See also Thymeleaf's TemplateMode enum.
     */
    private TemplateMode mode = TemplateMode.HTML;

    /**
     * Template files encoding.
     */
    private Charset encoding = DEFAULT_ENCODING;

    /**
     * Whether to enable template caching.
     */
    private boolean cache = true;

    /**
     * Order of the template resolver in the chain. By default, the template resolver is
     * first in the chain. Order start at 1 and should only be set if you have defined
     * additional "TemplateResolver" beans.
     */
    private Integer templateResolverOrder;



    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public TemplateMode getMode() {
        return mode;
    }

    public void setMode(TemplateMode mode) {
        this.mode = mode;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public Integer getTemplateResolverOrder() {
        return templateResolverOrder;
    }

    public void setTemplateResolverOrder(Integer templateResolverOrder) {
        this.templateResolverOrder = templateResolverOrder;
    }

}
