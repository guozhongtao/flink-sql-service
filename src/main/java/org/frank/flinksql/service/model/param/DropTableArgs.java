package org.frank.flinksql.service.model.param;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.frank.flinksql.service.service.task.TaskEventEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@ApiModel(description = "table ddl args")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropTableArgs implements Args {

    @ApiModelProperty(value = "dbName", required = true)
    @NotNull(message = "The value of 'dbName' can not be null")
    @Size(min = ArgumentConstant.STRING_LENGTH_1, max = ArgumentConstant.STRING_LENGTH_8192, message = "the length of 'dbName' should be between 0 and 8192")
    private String dbName;

    @ApiModelProperty(value = "tblName", required = true)
    @NotNull(message = "The value of 'tblName' can not be null")
    @Size(min = ArgumentConstant.STRING_LENGTH_1, max = ArgumentConstant.STRING_LENGTH_8192, message = "the length of 'tblName' should be between 0 and 8192")
    private String tblName;

    @Override
    public String buildCmdArgs() {
        return String.format("%s.%s", dbName, tblName);
    }


    @Override
    public TaskEventEnum getType() {
        return TaskEventEnum.DROP;
    }

    public List<String> parse(String cmdArgs) {
        List<String> res = Lists.newArrayList();
        Iterable<String> split = Splitter.on('.').split(cmdArgs);
        split.forEach(res::add);

        return res;
    }
}
