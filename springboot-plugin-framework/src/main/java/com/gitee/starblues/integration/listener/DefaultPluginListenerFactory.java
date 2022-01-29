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

package com.gitee.starblues.integration.listener;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的插件工厂
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginListenerFactory implements PluginListenerFactory{

    private final List<PluginListener> listeners;

    public DefaultPluginListenerFactory(ApplicationContext applicationContext){
        listeners = new ArrayList<>();
        addPluginListener(new SwaggerListener(applicationContext));
        addExtendPluginListener(applicationContext);
    }

    public DefaultPluginListenerFactory(){
        listeners = new ArrayList<>();
    }


    private void addExtendPluginListener(ApplicationContext applicationContext){
        List<PluginListener> pluginListeners = SpringBeanUtils.getBeans(applicationContext, PluginListener.class);
        listeners.addAll(pluginListeners);
    }

    @Override
    public synchronized void addPluginListener(PluginListener pluginListener) {
        if(pluginListener != null){
            listeners.add(pluginListener);
        }
    }

    @Override
    public List<PluginListener> getListeners() {
        return listeners;
    }

    @Override
    public void loadSuccess(PluginInfo pluginInfo) {
        for (PluginListener listener : listeners) {
            try {
                listener.loadSuccess(pluginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadFailure(Path path, Throwable throwable) {
        for (PluginListener listener : listeners) {
            try {
                listener.loadFailure(path, throwable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unLoadSuccess(PluginInfo pluginInfo) {
        for (PluginListener listener : listeners) {
            try {
                listener.unLoadSuccess(pluginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startSuccess(PluginInfo pluginInfo) {
        for (PluginListener listener : listeners) {
            try {
                listener.startSuccess(pluginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startFailure(PluginInfo pluginInfo, Throwable throwable) {
        for (PluginListener listener : listeners) {
            try {
                listener.startFailure(pluginInfo, throwable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopSuccess(PluginInfo pluginInfo) {
        for (PluginListener listener : listeners) {
            try {
                listener.stopSuccess(pluginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopFailure(PluginInfo pluginInfo, Throwable throwable) {
        for (PluginListener listener : listeners) {
            try {
                listener.stopFailure(pluginInfo, throwable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
