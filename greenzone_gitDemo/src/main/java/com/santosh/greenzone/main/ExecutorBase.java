package com.santosh.greenzone.main;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.santosh.greenzone.main.controller.StarToCopyProcessThread;

@Component
public class ExecutorBase {


    private static final Logger logger = LogManager.getLogger(ExecutorBase.class);

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;

    private Boolean debug = true;

    @PostConstruct
    public void atStartup() {
    	StarToCopyProcessThread classeTaskRunn = applicationContext.getBean(StarToCopyProcessThread.class);
        taskExecutor.execute(classeTaskRunn );
        if (debug) {
            logger.info("Running thread");
        }
    }
}
