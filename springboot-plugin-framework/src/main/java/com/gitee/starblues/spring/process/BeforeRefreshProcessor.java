package com.gitee.starblues.spring.process;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.OrderPriority;

/**
 * @author starBlues
 * @version 3.0.0
 */
public interface BeforeRefreshProcessor {

    void registry(SpringPluginRegistryInfo registryInfo);

    void unRegistry(SpringPluginRegistryInfo registryInfo);

    OrderPriority order();

}
