package com.basic.example.plugin1.extract;

import com.basic.example.main.extract.ExtractExample;
import com.gitee.starblues.annotation.Extract;

/**
 * @author starBlues
 * @version 1.0
 */
@Extract(bus = "PluginExtract1")
public class PluginExtract implements ExtractExample {
    @Override
    public void exe() {
        System.out.println(PluginExtract.class.getName());
    }

    @Override
    public void exe(String name) {
        System.out.println(PluginExtract.class.getName() + ": name");
    }

    @Override
    public void exe(Info info) {
        System.out.println(PluginExtract.class.getName() + ": " + info);
    }

    @Override
    public Info exeInfo(Info info) {
        info.setName("Plugin1-PluginExtract1");
        info.setAge(0);
        return info;
    }
}
