package com.jing.admin.core.workflow.log;

import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.service.WorkflowNodeLogService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 工作流节点日志队列服务
 * 用于异步处理工作流节点执行日志的保存
 */
@Component
public class WorkflowNodeLogQueueService {
    
    @Autowired
    private WorkflowNodeLogService workflowNodeLogService;
    
    // 日志任务队列
    private final BlockingQueue<WorkflowNodeLogTask> logQueue = new LinkedBlockingQueue<>();
    
    // 工作线程
    private Thread workerThread;
    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        // 启动日志处理工作线程
        workerThread = new Thread(this::processLogQueue, "WorkflowNodeLogProcessor");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @PreDestroy
    public void shutdown() {
        // 停止工作线程
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    /**
     * 添加日志任务到队列
     */
    public void addLogTask(WorkflowNodeLogTask task) {
        try {
            logQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 处理日志队列
     */
    private void processLogQueue() {
        while (running) {
            try {
                // 从队列中获取日志任务
                WorkflowNodeLogTask task = logQueue.take();
                
                // 根据操作类型处理日志
                switch (task.getOperationType()) {
                    case INSERT:
                        workflowNodeLogService.saveNodeLog(task.getNodeLog());
                        break;
                    case UPDATE:
                        workflowNodeLogService.saveNodeLog(task.getNodeLog());
                        break;
                    default:
                        // 未知操作类型，跳过
                        break;
                }
            } catch (InterruptedException e) {
                // 线程被中断，退出循环
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // 处理异常，但继续处理队列中的其他任务
                e.printStackTrace();
            }
        }
    }
}