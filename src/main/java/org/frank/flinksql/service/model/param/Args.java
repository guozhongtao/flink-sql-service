package org.frank.flinksql.service.model.param;

import org.frank.flinksql.service.service.task.TaskEventEnum;

public interface Args {

    TaskEventEnum getType();
    String buildCmdArgs();

}
