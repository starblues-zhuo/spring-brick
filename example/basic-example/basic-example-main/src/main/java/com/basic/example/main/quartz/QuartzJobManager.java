package com.basic.example.main.quartz;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.listener.PluginListener;
import org.quartz.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * QuartzJob 管理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class QuartzJobManager implements PluginListener {

    private final static String TRIGGER_GROUP = "QuartzTriggerGroup";
    private final static String JOB_GROUP = "QuartzJobGroup";


    private final Scheduler scheduler;
    private final PluginApplication pluginApplication;

    /**
     * 缓存启动的job. 用于停止时使用
     */
    private final Map<String, List<QuartzJob>> startJobMap = new HashMap<>();


    public QuartzJobManager(SchedulerFactory schedulerFactory,
                            PluginApplication pluginApplication) throws SchedulerException {
        this.scheduler = schedulerFactory.getScheduler();
        this.pluginApplication = pluginApplication;
    }


    @Override
    public void registry(String pluginId) {
        List<QuartzJob> quartzJobs = pluginApplication.getPluginUser().getPluginBeans(pluginId, QuartzJob.class);
        if(quartzJobs == null || quartzJobs.isEmpty()){
            return;
        }
        for (QuartzJob quartzJob : quartzJobs) {
            try {
                if(startJob(quartzJob)){
                    List<QuartzJob> quartzJobsList = startJobMap.get(pluginId);
                    if(quartzJobsList == null){
                        quartzJobsList = new ArrayList<>();
                        startJobMap.put(pluginId, quartzJobsList);
                    }
                    quartzJobsList.add(quartzJob);
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unRegistry(String pluginId) {
        List<QuartzJob> quartzJobs = startJobMap.remove(pluginId);
        if(quartzJobs == null){
            return;
        }
        for (QuartzJob quartzJob : quartzJobs) {
            try {
                stopJob(quartzJob);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failure(String pluginId, Throwable throwable) {

    }

    /**
     * 启动job
     * @param quartzJob job接口
     */
    private boolean startJob(QuartzJob quartzJob) throws SchedulerException {
        if(quartzJob == null){
            return false;
        }
        if(!quartzJob.enable()){
            // 不启用
            return false;
        }
        JobBuilder jobBuilder = JobBuilder.newJob(quartzJob.jobClass())
                .withIdentity(quartzJob.jobName(), JOB_GROUP);
        Map<String, Object> jobData = quartzJob.jobData();
        if(jobData != null){
            jobBuilder.setJobData(new JobDataMap(jobData));
        }
        JobDetail jobDetail = jobBuilder.build();
        Trigger trigger = configTrigger(quartzJob);
        scheduler.scheduleJob(jobDetail, trigger);
        synchronized (scheduler) {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        }
        return true;
    }


    /**
     * 停止job
     * @param quartzJob job接口
     */
    private void stopJob(QuartzJob quartzJob) throws SchedulerException {

        String jobName = quartzJob.jobName();
        String triggerName = quartzJob.triggerName();

        // 停止触发器
        scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, TRIGGER_GROUP));
        // 停止任务
        scheduler.pauseJob(JobKey.jobKey(jobName, JOB_GROUP));

        // 停止该触发器的任务
        scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, TRIGGER_GROUP));
        // 删除任务
        scheduler.deleteJob(JobKey.jobKey(jobName, JOB_GROUP));
    }


    /**
     * 配置触发器
     * @param quartzJob quartzJob
     * @return 触发器
     */
    private Trigger configTrigger(QuartzJob quartzJob) {
        //0 56 09 ? * *
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(quartzJob.triggerName(), TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.cron()));

        int delaySeconds = quartzJob.delaySeconds();
        if(delaySeconds <= 0L){
            triggerBuilder.startNow();
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            localDateTime = localDateTime.plusSeconds(60);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            triggerBuilder.startAt(date);
        }
        return triggerBuilder.build();
    }

}
