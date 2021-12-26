package com.gitee.starblues.spring;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * @author starBlues
 * @version 1.0
 */
public interface ApplicationContext extends AutowireCapableBeanFactory, ListableBeanFactory  {

}
