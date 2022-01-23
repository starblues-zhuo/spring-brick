package com.gitee.starblues.bootstrap.utils;

import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;

import java.util.List;
import java.util.function.Supplier;

/**
 * ProcessorUtils
 * @author starBlues
 * @version 3.0.0
 */
public class ProcessorUtils {

    public static void add(List<SpringPluginProcessor> pluginProcessors, Supplier<SpringPluginProcessor> supplier){
        try {
            SpringPluginProcessor pluginProcessor = supplier.get();
            pluginProcessors.add(pluginProcessor);
        } catch (Throwable e){
            // 忽略
        }
    }


}
