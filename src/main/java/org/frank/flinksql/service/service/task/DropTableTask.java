package org.frank.flinksql.service.service.task;

import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import org.frank.flinksql.service.model.param.Args;
import org.frank.flinksql.service.util.IOUtils;
import org.frank.flinksql.service.util.ShellCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class DropTableTask extends AbstractBaseTask {

    @Override
    public void run(Args args, CallBack callBack) {
        beforRun(args);
        CompletableFuture<List<String>> cf = CompletableFuture.supplyAsync(
                () -> ShellCmd.runCommand(buildCmd(), SHELL_CMD_PATH), executor);
        cf.thenAccept((result) -> {
            log.info("success run shell task: {}", result);
        });
        cf.exceptionally((e) -> {
            log.error("failed run shell task: {}", e.getMessage(), e);
            metricsTracker.recordShellFailed();
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        });

        try {
            List<String> res = cf.get(SHELL_TASK_TIMEOUT_SECOND, TimeUnit.SECONDS);
            log.info("run shell task res: {}", res);
        } catch (InterruptedException e) {
            log.error("shell task be interrupted : {}", e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("shell task failed : {}", e.getMessage(), e);
        } catch (TimeoutException e) {
            log.error("shell task timeout : {}", e.getMessage(), e);
        }

    }

    @Override
    public List<String> buildCmd() {
        final String argFile = "/tmp/cmd_drop_" + System.currentTimeMillis() + ".arg";
        final String cmdFile = "/tmp/drop_" + System.currentTimeMillis() + ".sh";
        IOUtils.writeToFile(argFile, String.format("DROP TABLE IF EXISTS  %s\n;\n", shellArgs.buildCmdArgs()));
        IOUtils.writeToFile(cmdFile, String.format(PROJECT_PATH + "/flink-1.13.1/bin/sql-client.sh -i "+ PROJECT_PATH + "/flink-1.13.1/init.sql -f %s", argFile));
        List<String> cmdlist = new ArrayList<>(Arrays.asList("sh", cmdFile));
        log.info("cmd list is {}", cmdlist);
        return cmdlist;
    }
}
