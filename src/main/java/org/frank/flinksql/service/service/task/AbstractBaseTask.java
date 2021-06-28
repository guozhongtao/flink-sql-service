package org.frank.flinksql.service.service.task;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.frank.flinksql.service.config.GatewayConfig;
import org.frank.flinksql.service.metric.JmxMetricsMeterRegistry;
import org.frank.flinksql.service.metric.MetricsTracker;
import org.frank.flinksql.service.model.param.Args;
import org.frank.flinksql.service.util.IOUtils;
import org.frank.flinksql.service.util.concurrency.NamedThreadFactory;
import org.frank.flinksql.service.util.concurrency.ThreadPoolUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public abstract class AbstractBaseTask implements Task, InitializingBean {

    @Getter
    @Setter
    protected Args shellArgs;

    @Autowired
    protected GatewayConfig gatewayConfig;

    protected ThreadPoolExecutor executor;

    protected MetricsTracker metricsTracker;

    protected static final int SHELL_TASK_TIMEOUT_SECOND = 600;

    protected static final String SHELL_CMD_PATH = "/tmp/";

    protected final static String PROJECT_PATH = System.getProperty("user.dir");

    protected void beforRun(Args args) {
        buildArgs(args);
        Preconditions.checkNotNull(getShellArgs(), "shellArgs can not be null");
        Preconditions.checkNotNull(executor, "executor can not be null");
    }

    protected void initExecutor() {
        metricsTracker = JmxMetricsMeterRegistry.create();
        RejectedExecutionHandler handler = (Runnable r, ThreadPoolExecutor executor) -> {
            log.error("Task:{} has been reject because of threadPool exhausted!" +
                            " pool:{}, active:{}, queue:{}, taskcnt: {}",
                    r,
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getTaskCount());
            throw new RejectedExecutionException("Callback handler thread pool has bean exhausted");
        };

        executor = ThreadPoolUtils.newCachedThreadPool(
                gatewayConfig.getTaskShellPoolCore(),
                gatewayConfig.getTaskShellPoolMax(),
                gatewayConfig.getTaskShellPoolKeepAliveTime(),
                ThreadPoolUtils.buildQueue(gatewayConfig.getTaskShellPoolQueue()),
                new NamedThreadFactory("shell-cmd-handler-pool"), handler);
    }

    protected void buildArgs(Args args){
        setShellArgs(args);
    }

    protected List<String> getCmds(String argFile, String cmdFile){
        IOUtils.writeToFile(argFile, String.format("%s\n\n", shellArgs.buildCmdArgs()));
        IOUtils.writeToFile(cmdFile, String.format(PROJECT_PATH + "/flink-1.13.1/bin/sql-client.sh -i "+ PROJECT_PATH + "/flink-1.13.1/init.sql -f %s", argFile));
        return ImmutableList.of("sh", cmdFile);
    }


    @Override
    public void afterPropertiesSet() {
        initExecutor();
    }

}
