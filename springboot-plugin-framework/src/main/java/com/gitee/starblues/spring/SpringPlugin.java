package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.factory.PluginFactory;

/**
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPlugin {


    void registry(PluginWrapper pluginWrapper) throws Exception;


    void unRegistry(String pluginId) throws Exception;


}
