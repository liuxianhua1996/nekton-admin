package com.ylys.datacenter.common.abs;

import com.ylys.datacenter.common.enums.ConstantEnum;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class AbstractJobTask<T, V> implements Runnable {
    protected String logId;
    protected String taskName;
    protected String taskId;
    protected T taskData;
    protected long startTime;
    protected long endTime;
    protected long expendTime;
    protected ConstantEnum status;
    protected Map<String, V> valueMap = new ConcurrentHashMap();
    @Override
    public abstract void run();
}
