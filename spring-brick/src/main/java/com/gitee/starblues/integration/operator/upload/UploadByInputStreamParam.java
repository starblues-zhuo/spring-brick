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
