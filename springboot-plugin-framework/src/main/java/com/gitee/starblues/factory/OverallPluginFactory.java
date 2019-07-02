package com.gitee.starblues.factory;

import com.gitee.starblues.exception.PluginFactoryException;
import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.factory.bean.DefaultPluginBeanFactory;
import com.gitee.starblues.utils.CommonUtils;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 总插件工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class OverallPluginFactory implements PluginFactory{

    private final static Logger LOG = LoggerFactory.getLogger(OverallPluginFactory.class);

    private final List<NoticePluginFactory> pluginNoticePluginFactoryList = new ArrayList<>(5);

    public OverallPluginFactory(ApplicationContext mainApplicationContext) {
        addNoticePluginFactory(mainApplicationContext);
        addExtension(mainApplicationContext);
        CommonUtils.order(pluginNoticePluginFactoryList, (noticePluginFactory -> noticePluginFactory.order()));
    }

    /**
     * 添加插件工厂
     * @param mainApplicationContext mainApplicationContext
     */
    private void addNoticePluginFactory(ApplicationContext mainApplicationContext){
        this.pluginNoticePluginFactoryList.add(new NoticePluginFactory(
                new DefaultPluginBeanFactory(mainApplicationContext)));
    }

    /**
     * 添加扩展
     * @param mainApplicationContext mainApplicationContext
     */
    private void addExtension(ApplicationContext mainApplicationContext) {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        Map<String, List<AbstractExtension>> pluginBeanRegisterMap = extensionFactory.getPluginExtension();
        pluginBeanRegisterMap.forEach((k, abstractExtensions)->{
            for (AbstractExtension abstractExtension : abstractExtensions) {
                List<NoticePluginFactory> noticePluginFactoryList = abstractExtension
                        .getNoticePluginFactory(mainApplicationContext);
                if(noticePluginFactoryList != null || !noticePluginFactoryList.isEmpty()){
                    this.pluginNoticePluginFactoryList.addAll(noticePluginFactoryList);
                }
            }
            LOG.info("Register Extension NoticePluginFactory : {}", k);
        });
    }


    @Override
    public FactoryType factoryType() {
        return MainFactoryType.OVERALL;
    }

    @Override
    public void registry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        for (NoticePluginFactory noticePluginFactory : pluginNoticePluginFactoryList) {
            noticePluginFactory.registry(pluginWrapper);
        }
    }

    @Override
    public void unRegistry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        for (NoticePluginFactory noticePluginFactory : pluginNoticePluginFactoryList) {
            noticePluginFactory.unRegistry(pluginWrapper);
        }
    }

    @Override
    public int order() {
        return 0;
    }

    /**
     * 添加通知插件工厂
     * @param noticePluginFactory noticePluginFactory
     */
    public synchronized void addNoticePluginFactory(NoticePluginFactory noticePluginFactory){
        if(noticePluginFactory != null){
            pluginNoticePluginFactoryList.add(noticePluginFactory);
        }
    }


}
