package org.frank.flinksql.service.service.task;

import org.frank.flinksql.service.exception.GWException;;
import org.frank.flinksql.service.model.param.Args;
import lombok.extern.slf4j.Slf4j;
import org.frank.flinksql.service.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskEventHandler {

    @Autowired
    private CreateTableTask createTableShellTask;

    @Autowired
    private DropTableTask dropTableShellTask;

    @Autowired
    private AlterTableTask alterTableShellTask;

    @Autowired
    private DeployTask deployTask;

    public <T> T run(Args args, CallBack<T> callBack) throws Exception {
        log.info("recive shell task event : {}", args);
        switch (args.getType()) {
            case CREATE:
                createTableShellTask.run(args, callBack);
                break;
            case DROP:
                dropTableShellTask.run(args, callBack);
                break;
            case ALTER:
                alterTableShellTask.run(args, callBack);
                break;
            case DEPLOY:
                deployTask.run(args, callBack);
                break;
            default:
                throw new GWException(ErrorCode.SYSTEM_ERROR);
        }
        return callBack != null ? callBack.getResult() : null;
    }
}
