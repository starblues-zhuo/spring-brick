package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Objects;

/**
 * 抽象的插件bean注册者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractPluginBeanRegister<T> implements PluginBeanRegister<T>{

    private final static Logger LOG = LoggerFactory.getLogger(AbstractPluginBeanRegister.class);

    protected final GenericApplicationContext applicationContext;


    public AbstractPluginBeanRegister(ApplicationContext mainApplicationContext)
            throws PluginBeanFactoryException{
        Objects.requireNonNull(mainApplicationContext);
        if(!(mainApplicationContext instanceof GenericApplicationContext)){
            throw new PluginBeanFactoryException("Registry PluginBean failure, " +
                    "because main <ApplicationContext> not is <GenericApplicationContext>");
        }
        this.applicationContext = (GenericApplicationContext) mainApplicationContext;
    }


}

