package org.frank.flinksql.service.core.checkpoint;


import org.frank.flinksql.service.core.enums.CheckPointParameterEnums;
import org.frank.flinksql.service.core.model.CheckPointParam;
import org.frank.flinksql.service.core.model.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;

@Slf4j
public class CheckPointParams {


    public static CheckPointParam buildCheckPointParam(ParameterTool parameterTool) {

        if (!parameterTool.getBoolean(CheckPointParameterEnums.checkpointEnable.name(), false)){
            return null;
        }
        String checkpointingMode = parameterTool.get(CheckPointParameterEnums.checkpointingMode.name(),
                CheckpointingMode.EXACTLY_ONCE.name());

        long checkpointInterval = parameterTool.getLong(CheckPointParameterEnums.checkpointInterval.name(),
                SystemConstant.DEFALUT_CHECKPOINT_INTERVAL);

        long checkpointTimeout = parameterTool.getLong(CheckPointParameterEnums.checkpointTimeout.name(), SystemConstant.DEFALUT_CHECKPOINT_TIMEOUT);
        int tolerableCheckpointFailureNumber =
                parameterTool.getInt(CheckPointParameterEnums.tolerableCheckpointFailureNumber.name(), SystemConstant.DEFALUT_TOLERABLE_CHECKPOINT_FAILURE_NUMBER);
        CheckPointParam checkPointParam = new CheckPointParam();

        checkPointParam.setCheckpointingMode(checkpointingMode);
        checkPointParam.setCheckpointInterval(checkpointInterval);
        checkPointParam.setCheckpointTimeout(checkpointTimeout);
        checkPointParam.setTolerableCheckpointFailureNumber(tolerableCheckpointFailureNumber);
        log.info("checkPointParam {}", checkPointParam);
        return checkPointParam;

    }

}
