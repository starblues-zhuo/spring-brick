package com.basic.example.plugin1.service;

import com.basic.example.main.invokeapi.CommonParam;
import com.basic.example.main.invokeapi.CommonReturn;
import com.basic.example.plugin1.config.PluginConfig1;
import com.gitee.starblues.annotation.Supplier;

/**
 * 被调用者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Supplier("SupplierService")
public class SupplierService {


    private final PluginConfig1 pluginConfig1;

    public SupplierService(PluginConfig1 pluginConfig1) {
        this.pluginConfig1 = pluginConfig1;
    }

    @Supplier.Method("getConfig")
    public PluginInfo getConfig(String key){
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setKey(key);
        pluginInfo.setName("my name is plugin1");
        pluginInfo.setConfig(pluginConfig1.toString());
        return pluginInfo;
    }

    public Integer add(Integer a1, Integer a2){
        return a1 + a2;
    }

    @Supplier.Method("call")
    public String call(CallerInfo callerInfo, String key){
        System.out.println(callerInfo);
        return key;
    }


    public CommonReturn commonTest(String key, CommonParam commonParam){
        System.out.println(commonParam);
        CommonReturn commonReturn = new CommonReturn();
        commonReturn.setReturnName(key);
        commonReturn.setResult(true);
        return commonReturn;
    }

    public static class CallerInfo{
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

        @Override
        public String toString() {
            return "CallerInfo{" +
                    "name='" + name + '\'' +
                    ", pluginInfo=" + pluginInfo +
                    '}';
        }
    }


    public static  class PluginInfo{
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

        @Override
        public String toString() {
            return "PluginInfo{" +
                    "key='" + key + '\'' +
                    ", name='" + name + '\'' +
                    ", config='" + config + '\'' +
                    '}';
        }
    }
}
