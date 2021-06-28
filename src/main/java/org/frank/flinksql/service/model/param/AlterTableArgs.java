package org.frank.flinksql.service.model.param;


import org.frank.flinksql.service.service.task.TaskEventEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(description = "table alter args")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterTableArgs implements Args {

    @ApiModelProperty(value = "ALTER TABLE", example = "", required = true)
    @NotNull(message = "The value of 'alterTableSql' can not be null")
    @Size(min = ArgumentConstant.STRING_LENGTH_1, max = ArgumentConstant.STRING_LENGTH_8192, message = "the length of 'CreateTableSql' should be between 0 and 4096")
    private String alterTableSql;

    @Override
    public TaskEventEnum getType() {
        return TaskEventEnum.ALTER;
    }

    @Override
    public String buildCmdArgs() {
        return alterTableSql;
    }
}
