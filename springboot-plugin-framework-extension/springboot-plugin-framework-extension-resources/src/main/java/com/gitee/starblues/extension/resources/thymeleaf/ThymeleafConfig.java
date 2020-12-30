package com.gitee.starblues.extension.resources.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangzhuo
 * @version 2.3
 */
public class ThymeleafConfig {


    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    public static final String DEFAULT_PREFIX = "templates/";

    public static final String DEFAULT_SUFFIX = ".html";

    /**
     * 存放模板引擎的前缀
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * 模板引擎文件的后缀
     */
    private String suffix = DEFAULT_SUFFIX;

    /**
     * 模型引入的模型
     * @see TemplateMode
     */
    private TemplateMode mode = TemplateMode.HTML;

    /**
     * 模板引擎的编码
     */
    private Charset encoding = DEFAULT_ENCODING;

    /**
     * 是否启用模板引擎的缓存
     */
    private boolean cache = true;

    /**
     * 模板解析器的执行顺序, 数字越小越先执行
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
