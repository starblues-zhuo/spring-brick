package com.basic.example.plugin1.config;

import com.basic.example.plugin1.job.QuartzJob1;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @Author rockstal
 * @Date 2021/4/22 19:18
 **/
@Configuration
public class QuartzJobConfiguration {


    @Bean
    public JobDetail jobDetail1(){
        return JobBuilder.newJob(QuartzJob1.class).storeDurably().build();
    }

    @Bean
    public Trigger trigger1(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(1) //每一秒执行一次
                .repeatForever(); //永久重复，一直执行下去
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail1())
                .withSchedule(scheduleBuilder)
                .build();
    }

}
