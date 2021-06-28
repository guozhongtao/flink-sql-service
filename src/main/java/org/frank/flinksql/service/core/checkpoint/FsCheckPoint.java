package org.frank.flinksql.service.core.checkpoint;


import org.frank.flinksql.service.core.config.Configurations;
import org.frank.flinksql.service.core.model.CheckPointParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.frank.flinksql.service.core.enums.StateBackendEnum;

import java.io.IOException;

@Slf4j
public class FsCheckPoint {

    public static void setCheckpoint(TableEnvironment tEnv, StreamExecutionEnvironment env, CheckPointParam checkPointParam) throws Exception {
        if (checkPointParam == null) {
            log.warn("===================Checkpoint can not be supported===================");
            return;
        }

        //保留多少份checkpoint
        Configurations.setConfigurationInt(tEnv, CheckpointingOptions.MAX_RETAINED_CHECKPOINTS.key(), 5);

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        //开始一致性模式：精确一次 exactly-once
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode()) ||
                CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())) {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            log.info("Checkpointing Mode exactly-once");
        } else {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
            log.info("Checkpointing Mode AT_LEAST_ONCE");
        }

        //默认超时10 minutes.
        checkpointConfig.setCheckpointTimeout(checkPointParam.getCheckpointTimeout());
        //确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        checkpointConfig.setMaxConcurrentCheckpoints(2);

        //设置失败次数
        checkpointConfig.setTolerableCheckpointFailureNumber(checkPointParam.getTolerableCheckpointFailureNumber());


        //设置后端状态
        setStateBackend(env, checkPointParam);

        //检查点在作业取消后的保留策略，DELETE_ON_CANCELLATION代表删除，RETAIN_ON_CANCELLATION代表保留
        if (checkPointParam.getExternalizedCheckpointCleanup() != null) {
            if (checkPointParam.getExternalizedCheckpointCleanup().
                    equalsIgnoreCase(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION.name())) {
                env.getCheckpointConfig()
                        .enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
                log.info("DELETE_ON_CANCELLATION param is min remove the checkpoint file");
            } else if (checkPointParam.getExternalizedCheckpointCleanup().
                    equalsIgnoreCase(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION.name())) {
                env.getCheckpointConfig()
                        .enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
                log.info("RETAIN_ON_CANCELLATION is min keep the checkpoint file");
            }
        } else {
            log.info("set the default retained strategy to keep the checkpoint file");
            env.getCheckpointConfig()
                    .enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        }

    }

    private static void setStateBackend(StreamExecutionEnvironment env, CheckPointParam checkPointParam) throws IOException {
        env.setStateBackend(new HashMapStateBackend());
        switch (checkPointParam.getStateBackendEnum()) {
            case StateBackendEnum.MEMORY:
                log.info("checkpoint is memory");
                env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
                break;
            case StateBackendEnum.FILE:
            default:
                log.info("checkpoint is file");
                env.getCheckpointConfig().setCheckpointStorage("hdfs:///checkpoints-data/");
                break;
        }
    }

}
