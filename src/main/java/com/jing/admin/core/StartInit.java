package com.jing.admin.core;

import com.jing.admin.core.workflow.WorkflowTestApplication;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author lxh
 * @date 2025/11/6
 **/
@Component
public class StartInit implements  ApplicationRunner {
    @Autowired
    WorkflowTestApplication workflowTestApplication;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        workflowTestApplication.test();
    }
}
