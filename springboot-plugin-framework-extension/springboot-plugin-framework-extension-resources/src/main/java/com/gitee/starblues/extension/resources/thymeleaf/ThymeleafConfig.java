package com.gitee.starblues.extension.resources.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author starBlues
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
    private Boolean cache = true;

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

    public void setMode(String mode) {
        this.mode = TemplateMode.parse(mode);
    }

    public Charset getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Integer getTemplateResolverOrder() {
        return templateResolverOrder;
    }

    public void setTemplateResolverOrder(Integer templateResolverOrder) {
        this.templateResolverOrder = templateResolverOrder;
    }

    @Override
    public String toString() {
        return "ThymeleafConfig{" +
                "prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", mode=" + mode.name() +
                ", encoding=" + encoding.name() +
                ", cache=" + cache +
                ", templateResolverOrder=" + templateResolverOrder +
                '}';
    }
}
