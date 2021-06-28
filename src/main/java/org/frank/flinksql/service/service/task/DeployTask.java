package org.frank.flinksql.service.service.task;

import com.google.common.collect.ImmutableMap;
import org.frank.flinksql.service.core.enums.CheckPointParameterEnums;
import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import org.frank.flinksql.service.model.param.Args;
import org.frank.flinksql.service.model.param.SubmitSqlJobArgs;
import org.frank.flinksql.service.util.IOUtils;
import org.frank.flinksql.service.util.ShellCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.frank.flinksql.service.enums.JobTypeEnum;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class DeployTask extends AbstractBaseTask {

    private static final String APP_CLASS_NAME = "com.tuhu.streamservice.gateway.core.SqlApplication";

    private String jar_path = "/tmp/stream_service-1.0.0-SNAPSHOT.jar";
    {
        ApplicationHome applicationHome = new ApplicationHome(getClass());
        File file = applicationHome.getSource();
        if (file != null){
            jar_path= file.getAbsolutePath();
        }
    }

    @Override
    public void run(Args args, CallBack callBack) {
        beforRun(args);
        CompletableFuture<List<String>> cf = CompletableFuture.supplyAsync(
                () -> ShellCmd.runCommand(buildCmd(), SHELL_CMD_PATH), executor);
        cf.thenAccept((result) -> {
            String jobId = null;
            String appId = null;
            String webUrl = null;
            if (CollectionUtils.isNotEmpty(result)){

                String successResult = result.get(0);
                String uri = successResult.substring(successResult.indexOf("Web Interface") + 14).split("\n")[0];
                if (successResult.indexOf("Web Interface") != -1){
                    String[] webUrlParse = successResult.substring(successResult.indexOf("Web Interface") + 14).split("\n");
                    if (webUrlParse != null && webUrlParse.length >= 1){
                        webUrl = webUrlParse[0].substring(0, uri.indexOf("of")-1);
                    }
                }

                if (successResult.indexOf("-kill") != -1){
                    String[] appIdParse = successResult.substring(successResult.indexOf("-kill") + 6).split("\n");
                    if (appIdParse != null && appIdParse.length >= 1){
                        appId = appIdParse[0];
                    }
                }
                if (successResult.indexOf("JobID") != -1){
                    String[] jobIdParse = successResult.substring(successResult.indexOf("JobID") + 6).split("\n");
                    if (jobIdParse != null && jobIdParse.length >=1){
                        jobId = jobIdParse[0];
                    }
                }
            }
            if(jobId == null || appId == null || webUrl == null){
                log.info("failed to run shell task: {}", result);
                throw new GWException("task submit failed");
            }
            callBack.setResult(ImmutableMap.of("appId", appId, "jobId", jobId, "webUrl", webUrl));
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
        final String argFile = "/tmp/cmd_deploy_" + System.currentTimeMillis() + ".arg";
        final String cmdFile = "/tmp/deploy_" + System.currentTimeMillis() + ".sh";
        IOUtils.writeToFile(argFile, String.format("%s\n\n", shellArgs.buildCmdArgs()));
        String command = buildRunCommandForYarnCluster((SubmitSqlJobArgs)shellArgs, argFile);
        //IOUtils.writeToFile(cmdFile, String.format(PROJECT_PATH + "/flink-1.13.1/bin/flink run %s", command));
        IOUtils.writeToFile(cmdFile, String.format("/Users/wangkai/apps/install/flink-1.13.1/bin/flink run %s", command));

        List<String> cmdlist = new ArrayList<>(Arrays.asList("sh", cmdFile));
        log.info("cmd list is {}", cmdlist);
        return cmdlist;
    }

    private String buildRunCommandForYarnCluster(SubmitSqlJobArgs submitSqlJobArgs, String argFile) {
        StringBuilder command = new StringBuilder();
        if (StringUtils.isNotEmpty(submitSqlJobArgs.getSavepointPath())) {
            command.append(" -s ").append(submitSqlJobArgs.getSavepointPath());
        }
        command.append(" -yjm ").append(submitSqlJobArgs.getJm());
        command.append(" -ytm ").append(submitSqlJobArgs.getTm());
        command.append(" -p ").append(submitSqlJobArgs.getParallelism());
        command.append(" -ys ").append(submitSqlJobArgs.getSlot());
        command.append(" -yqu ").append(submitSqlJobArgs.getYarnQueue());
        command.append(" -ynm ").append(submitSqlJobArgs.getAppName());
        command.append(" -yd -m yarn-cluster");
        switch (submitSqlJobArgs.getJobTypeEnum()) {
            case JobTypeEnum.SQL:
                command.append(" -c ").append(APP_CLASS_NAME);
                //command.append(" ").append(jar_path);
                command.append(" ").append("/Users/wangkai/apps/src/codetest/stream_service/target/stream_service-1.0.0-SNAPSHOT-SqlApplication.jar");
                command.append(" -sql ").append(argFile);
                //command.append(" -pPath ").append(PROJECT_PATH);
                command.append(" -pPath ").append("/Users/wangkai/apps/install/hive-2.3.8-client");
                command.append(" -appName ").append(submitSqlJobArgs.getAppName());
                if (submitSqlJobArgs.isEnableCheckPoint()) {
                    command.append(" -").append(CheckPointParameterEnums.checkpointEnable.name()).append(" ").append("true");
                    command.append(" -").append(CheckPointParameterEnums.checkpointingMode.name()).append(" ").append(submitSqlJobArgs.getCheckPointMode());
                    command.append(" -").append(CheckPointParameterEnums.checkpointInterval.name()).append(" ").append(submitSqlJobArgs.getTimeInterval());
                    command.append(" -").append(CheckPointParameterEnums.checkpointTimeout.name()).append(" ").append(submitSqlJobArgs.getTimeout());
                }
                break;
            case JobTypeEnum.JAR:
                throw new GWException("the mod can not support");
        }

        log.info("buildRunCommandForYarnCluster runCommand={}", command);
        return command.toString();
    }
}
