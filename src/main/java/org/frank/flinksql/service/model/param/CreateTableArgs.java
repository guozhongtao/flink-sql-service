package org.frank.flinksql.service.model.param;


import org.frank.flinksql.service.service.task.TaskEventEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(description = "table ddl args")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableArgs implements Args {

    @ApiModelProperty(value = "CREATE TABLE DDL", example = "CREATE TABLE mykafka (name String, age Int) WITH (\n" +
            "'connector.type' = 'kafka',\n" +
            "'connector.version' = 'universal',\n" +
            "'connector.topic' = 'test',\n" +
            "'connector.properties.zookeeper.connect' = 'localhost:2181',\n" +
            "'connector.properties.bootstrap.servers' = 'localhost:9092',\n" +
            "'format.type' = 'csv',\n" +
            "'update-mode' = 'append'\n" +
            ");", required = true)
    @NotNull(message = "The value of 'createTableSql' can not be null")
    @Size(min = ArgumentConstant.STRING_LENGTH_1, max = ArgumentConstant.STRING_LENGTH_8192, message = "the length of 'CreateTableSql' should be between 0 and 4096")
    private String createTableSql;

    @Override
    public TaskEventEnum getType() {
        return TaskEventEnum.CREATE;
    }

    @Override
    public String buildCmdArgs() {
        return createTableSql;
    }
}
