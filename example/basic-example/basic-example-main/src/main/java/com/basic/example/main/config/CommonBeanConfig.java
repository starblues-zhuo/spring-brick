package com.basic.example.main.config;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公用bean的配置
 *
 * @author starBlues
 * @version 1.0
 */
@Configuration
public class CommonBeanConfig {

    @Bean
    public SchedulerFactory schedulerFactory(){
        return new StdSchedulerFactory();
    }

}
