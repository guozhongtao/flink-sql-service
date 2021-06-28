package org.frank.flinksql.service.model.param;

import org.frank.flinksql.service.enums.JobTypeEnum;
import org.frank.flinksql.service.service.task.TaskEventEnum;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel(description = "submit job args")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSqlJobArgs implements Args {

    @NotNull(message = "The value of 'submit sql' can not be null")
    private String sql;

    private String yarnCluster;

    @NotNull(message = "The value of 'submit sql' can not be null")
    private String yarnQueue;

    @NotNull(message = "The value of 'submit sql' can not be null")
    private int parallelism;

    @NotNull(message = "The value of 'submit sql' can not be null")
    private String jm;

    @NotNull(message = "The value of 'submit sql' can not be null")
    private String tm;

    @NotNull(message = "The value of 'submit sql' can not be null")
    private int slot;

    private String appName;

    private String checkPointMode;

    //checkPoint time interval
    private int timeInterval;

    private int timeout;

    private boolean isEnableCheckPoint;

    private String savepointPath;

    private JobTypeEnum jobTypeEnum = JobTypeEnum.SQL;



    @Override
    public String buildCmdArgs() {
        return sql;
    }

    @Override
    public TaskEventEnum getType() {
        return TaskEventEnum.DEPLOY;
    }
}
