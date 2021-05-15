package com.gitee.starblues.extension.log;


import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志扩展
 * @author sousouki starBlues
 * @version 2.4.3
 */
public class SpringBootLogExtension extends AbstractExtension {

    private static final String KEY = "SpringBootLogExtension";

    private final Type type;

    public SpringBootLogExtension(Type type){
        this.type = type;
    }


    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext applicationContext) {
        List<PluginPipeProcessorExtend> pipeProcessorExtends = new ArrayList<>();
        pipeProcessorExtends.add(new PluginLogConfigProcessor(type));
        return pipeProcessorExtends;
    }

    public enum Type{
        /**
         * 集成log4j
         **/
        LOG4J,

        /**
         * 集成 logback
         **/
        LOGBACK
    }
}
