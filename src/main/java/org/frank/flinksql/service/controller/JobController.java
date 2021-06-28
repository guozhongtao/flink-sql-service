package org.frank.flinksql.service.controller;


import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.model.param.SubmitSqlJobArgs;
import org.frank.flinksql.service.service.JobService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/job")
@Slf4j
public class JobController extends BaseController {

    @Autowired
    private JobService jobService;


    @ApiOperation(value="submit job", httpMethod = HTTP_POST)
    @PostMapping(value = "/sql/submit")
    public Result submitSql(@RequestBody @Valid SubmitSqlJobArgs args) {
        log.info("METHOD [submitSql] BEGINS => {}", args);
        return jobService.submitSql(args);
    }


    @ApiOperation(value="stop job", httpMethod = HTTP_POST)
    @PostMapping(value = "/stop")
    public Result stopJob(@RequestParam  String appId) {
        log.info("METHOD [stopJob] BEGINS => {}", appId);
        return jobService.stopJob(appId);
    }

    @ApiOperation(value="cancel job", httpMethod = HTTP_POST)
    @PostMapping("/cancel")
    public Result cancelJob(@RequestParam String jobId, @RequestParam String appId) {
        log.info("METHOD [cancelJob] BEGINS => {}", jobId);
        return jobService.cancelJob(jobId, appId);
    }

    @ApiOperation(value="cancel job", httpMethod = HTTP_POST)
    @PostMapping("/savepoint")
    public Result savepoint(String jobId) {
        log.info("METHOD [savepoint] BEGINS => {}", jobId);
        return jobService.savepoint(jobId);
    }
}
