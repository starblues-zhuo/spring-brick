/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.spring.web.thymeleaf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 插件 Thymeleaf 配置
 * @author starBlues
 * @version 3.0.0
 */
public class ThymeleafConfig {

    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    public static final String DEFAULT_PREFIX = "templates/";

    public static final String DEFAULT_SUFFIX = ".html";

    private boolean enabled = true;

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
     * HTML、XML、TEXT、JAVASCRIPT、CSS、RAW
     */
    private String mode = "HTML";

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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

}
