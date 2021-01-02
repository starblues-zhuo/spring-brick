package com.basic.example.main.quartz;

import org.quartz.Job;

import java.util.Map;

/**
 * Quartz框架job定义的统一接口
 *
 * @author starBlues
 * @version 1.0
 */
public interface QuartzJob {

    /**
     * 是否启用
     * @return true 启用。false 禁用
     */
    boolean enable();


    /**
     * job 名称
     * @return String
     */
    String jobName();

    /**
     * 触发器名称
     * @return String
     */
    String triggerName();

    /**
     * cron 表达式
     * @return  cron 表达式
     */
    String cron();

    /**
     * 延迟执行秒数
     * @return 秒数
     */
    int delaySeconds();


    /**
     * job 执行类型
     * @return Job 实现类
     */
    Class<? extends Job>jobClass();


    /**
     * 传入到job中的数据
     * @return Map
     */
    Map<String, Object> jobData();




}
