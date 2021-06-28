package org.frank.flinksql.service.core;


import org.frank.flinksql.service.core.checkpoint.CheckPointParams;
import org.frank.flinksql.service.core.checkpoint.FsCheckPoint;
import org.frank.flinksql.service.core.config.Configurations;
import org.frank.flinksql.service.core.execute.ExecuteSql;
import org.frank.flinksql.service.core.execute.SqlFileParser;
import org.frank.flinksql.service.core.model.JobRunParam;
import org.frank.flinksql.service.core.model.SqlCommandCall;
import org.frank.flinksql.service.core.model.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.calcite.shaded.com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SqlApplication {

    private static final Logger log = LoggerFactory.getLogger(SqlApplication.class);

    public static void main(String[] args) {

        try {
            Arrays.stream(args).forEach((arg) -> log.info("********user define parameter is {}*******", arg));

            JobRunParam jobRunParam = buildParam(args);
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            EnvironmentSettings settings = EnvironmentSettings.newInstance()
                    .useBlinkPlanner()
                    .inStreamingMode()
                    .build();

            StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);

            //set job name
            Configurations.setConfigurationString(tableEnv, "pipeline.name", jobRunParam.getAppName());
            HiveCatalog hive = new HiveCatalog("myhive", "default",jobRunParam.getProjectPath()+"/conf");
            tableEnv.registerCatalog("myhive", hive);
            tableEnv.useCatalog("myhive");

            //set checkPoint
            FsCheckPoint.setCheckpoint(tableEnv, env, jobRunParam.getCheckPointParam());

            List<String> sql = Files.readAllLines(Paths.get(jobRunParam.getSqlPath()));

            List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);

            StatementSet statementSet = tableEnv.createStatementSet();

            ExecuteSql.exeSql(sqlCommandCallList, tableEnv, statementSet);

            TableResult tableResult = statementSet.execute();
            if (tableResult == null || tableResult.getJobClient().get() == null ||
                    tableResult.getJobClient().get().getJobID() == null) {
                throw new RuntimeException("任务运行失败 没有获取到JobID");
            }
            JobID jobID = tableResult.getJobClient().get().getJobID();
            log.info(SystemConstant.QUERY_JOBID_KEY_WORD + "{}",jobID);
            //env.execute("test");
        } catch (Exception e) {
            log.error("任务执行失败：", e);
        }
    }


    private static JobRunParam buildParam(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String sqlPath = parameterTool.get("sql");
        Preconditions.checkNotNull(sqlPath, "-sql param can not be null");
        JobRunParam jobRunParam = new JobRunParam();
        jobRunParam.setSqlPath(sqlPath);
        jobRunParam.setProjectPath(parameterTool.get("pPath"));
        jobRunParam.setAppName(parameterTool.get("appName"));
        if (StringUtils.isNotEmpty(parameterTool.get("zk"))) {
            jobRunParam.setStorageDir(parameterTool.get("zk"));
        }
        if (StringUtils.isNotEmpty(parameterTool.get("store"))){
            jobRunParam.setStorageDir(parameterTool.get("store"));
        }
        jobRunParam.setCheckPointParam(CheckPointParams.buildCheckPointParam(parameterTool));
        return jobRunParam;
    }
}
