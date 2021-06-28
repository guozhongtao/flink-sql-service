package org.frank.flinksql.service.service;


import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.model.param.SubmitSqlJobArgs;
import org.frank.flinksql.service.service.task.CallBack;
import org.frank.flinksql.service.service.task.TaskEventHandler;
import org.frank.flinksql.service.util.ResultWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobService {

    @Autowired
    private TaskEventHandler taskEventHandler;

    @Autowired
    private YarnService yarnService;

    public Result submitSql(SubmitSqlJobArgs args) {
        try{
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
            return ResultWrapper.successResultWrapper(object);
        } catch (Exception e) {
            log.error("");
        }
        return ResultWrapper.errorResultWrapper();
    }

    public Result stopJob(String applicationId) {
        yarnService.stopJobByAppId(applicationId);
        return ResultWrapper.successResultWrapper();
    }

    public Result cancelJob(String jobId, String applicationId) {
        yarnService.cancelJobForYarnByAppId(applicationId, jobId);
        return stopJob(applicationId);
    }

    public Result savepoint(String jobId) {
        return null;
    }

    public Result getJobStatus(String jobId) {
        return null;
    }
}
