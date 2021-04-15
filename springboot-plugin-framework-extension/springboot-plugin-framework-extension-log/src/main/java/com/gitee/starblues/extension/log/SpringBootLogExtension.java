package com.gitee.starblues.extension.log;


import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志扩展
 * @author sousouki
 * @version 2.4.3
 */
public class SpringBootLogExtension extends AbstractExtension {

    public static final String KEY = "SpringBootLogExtension";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext applicationContext) {
        List<PluginPipeProcessorExtend> pipeProcessorExtends = new ArrayList<>();
        pipeProcessorExtends.add(new PluginLogConfigProcessor());
        return pipeProcessorExtends;
    }
}
