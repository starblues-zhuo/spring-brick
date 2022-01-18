package com.gitee.starblues.integration.operator.upload;

import com.gitee.starblues.utils.Assert;

import java.io.InputStream;

/**
 * InputStream 上传插件参数
 * @author starBlues
 * @version 3.0.0
 */
public class UploadByInputStreamParam extends UploadParam{

    /**
     * 插件文件名称
     */
    private final String pluginFileName;

    /**
     * 插件输入流
     */
    private final InputStream inputStream;

    public UploadByInputStreamParam(String pluginFileName, InputStream inputStream) {
        this.pluginFileName = Assert.isNotEmpty(pluginFileName, "参数pluginFileName不能为空");
        this.inputStream = Assert.isNotNull(inputStream, "参数inputStream不能为空");
    }

    public String getPluginFileName() {
        return pluginFileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
