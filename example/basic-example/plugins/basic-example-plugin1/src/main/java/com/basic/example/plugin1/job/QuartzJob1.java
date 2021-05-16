package com.basic.example.plugin1.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description TODO
 * @Author rockstal
 * @Date 2021/4/22 19:18
 **/
public class QuartzJob1 extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(QuartzJob1.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("QuartzJob1---{}", sdf.format(new Date()));
    }

}
