package org.frank.flinksql.service.service.task;

import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import org.frank.flinksql.service.model.param.Args;
import org.frank.flinksql.service.util.ShellCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class CreateTableTask extends AbstractBaseTask {

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
            if (null != callBack){
                callBack.setResult(e.getMessage());
            }
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
        final String argFile = "/tmp/cmd_create_" + System.currentTimeMillis() + ".arg";
        final String cmdFile = "/tmp/cmd_create_" + System.currentTimeMillis() + ".sh";
        List<String> cmdlist = getCmds(argFile, cmdFile);
        log.info("cmd list is {}", cmdlist);
        return cmdlist;
    }
}
