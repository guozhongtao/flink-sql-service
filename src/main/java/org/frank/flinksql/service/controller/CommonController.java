package org.frank.flinksql.service.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class CommonController extends BaseController {

    @ApiOperation(value = "health check", httpMethod = HTTP_GET)
    @GetMapping(value = "/_health")
    public String healthCheck() {
        log.info("METHOD [healthCheck] BEGINS =>, params = none");
        return "ok";
    }

}
