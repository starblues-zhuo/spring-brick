package com.gitee.starblues.integration.operator.upload;

import com.gitee.starblues.utils.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传插件参数
 * @author starBlues
 * @version 1.0
 */
public class UploadByMultipartFileParam extends UploadParam{

    private final MultipartFile pluginMultipartFile;

    public UploadByMultipartFileParam(MultipartFile pluginMultipartFile) {
        this.pluginMultipartFile = Assert.isNotNull(pluginMultipartFile, "参数pluginMultipartFile不能为空");
    }

    public MultipartFile getPluginMultipartFile() {
        return pluginMultipartFile;
    }
}
