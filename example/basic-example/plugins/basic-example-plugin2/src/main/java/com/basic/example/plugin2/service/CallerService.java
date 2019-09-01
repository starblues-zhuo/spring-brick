package com.basic.example.plugin2.service;

import com.basic.example.main.invokeapi.CommonParam;
import com.basic.example.main.invokeapi.CommonReturn;
import com.gitee.starblues.annotation.Caller;

/**
 * 调用插件plugin1中的 com.plugin.example.plugin1.service.SupplierService 接口定义
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Caller("SupplierService")
public interface CallerService {

    PluginInfo getConfig(String key);

    Integer add(Integer a1, Integer a2);

    @Caller.Method("call")
    String test(CallerInfo callerInfo, String key);

    CommonReturn commonTest(String key, CommonParam commonParam);

    class CallerInfo{
        private String name;
        private PluginInfo pluginInfo;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PluginInfo getPluginInfo() {
            return pluginInfo;
        }

        public void setPluginInfo(PluginInfo pluginInfo) {
            this.pluginInfo = pluginInfo;
        }
    }


    class PluginInfo{
        private String key;
        private String name;
        private String config;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }
    }

}
