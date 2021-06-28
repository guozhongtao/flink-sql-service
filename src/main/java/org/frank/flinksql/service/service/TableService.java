package org.frank.flinksql.service.service;


import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.model.param.AlterTableArgs;
import org.frank.flinksql.service.model.param.CreateTableArgs;
import org.frank.flinksql.service.model.param.DropTableArgs;
import org.frank.flinksql.service.service.task.CallBack;
import org.frank.flinksql.service.service.task.TaskEventHandler;
import org.frank.flinksql.service.util.ResultWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TableService {

    @Autowired
    private TaskEventHandler taskEventHandler;

    public Result createTable(CreateTableArgs args) {

        try {
            Object object = taskEventHandler.run(args, new CallBack<Object>() {
                Object object;
                @Override
                public void setResult(Object object) {
                    this.object = object;
                }

                @Override
                public Object getResult() {
                    return object;
                }
            });
            return ResultWrapper.shellExecuteResult(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        }
    }

    public Result dropTable(DropTableArgs args) {
        try {
            Object object = taskEventHandler.run(args, new CallBack<Object>() {
                Object object;
                @Override
                public void setResult(Object object) {
                    this.object = object;
                }

                @Override
                public Object getResult() {
                    return object;
                }
            });
            return ResultWrapper.shellExecuteResult(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        }
    }

    public Result alterTable(AlterTableArgs args) {
        try {
            Object object = taskEventHandler.run(args, new CallBack<Object>() {
                Object object;
                @Override
                public void setResult(Object object) {
                    this.object = object;
                }

                @Override
                public Object getResult() {
                    return object;
                }
            });
            return ResultWrapper.shellExecuteResult(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        }
    }
}
