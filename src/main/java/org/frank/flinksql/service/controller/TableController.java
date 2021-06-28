package org.frank.flinksql.service.controller;


import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.model.param.AlterTableArgs;
import org.frank.flinksql.service.model.param.CreateTableArgs;
import org.frank.flinksql.service.model.param.DropTableArgs;
import org.frank.flinksql.service.service.TableService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class TableController extends BaseController {

    @Autowired
    private TableService tableService;

    @ApiOperation(value = "create table", httpMethod = HTTP_POST)
    @PostMapping(value = "/ddl/table/create")
    public Result createTable(@RequestBody @Valid CreateTableArgs args) {
        log.info("METHOD [createTable] BEGINS => {}", args);
        return tableService.createTable(args);
    }

    @ApiOperation(value = "alter table", httpMethod = HTTP_POST)
    @PostMapping(value = "/ddl/table/alter")
    public Result alterTable(@RequestBody @Valid AlterTableArgs args) {
        log.info("METHOD [alterTable] BEGINS => {}", args);
        return tableService.alterTable(args);
    }


    // DROP TABLE IF EXISTS
    @ApiOperation(value = "drop table", httpMethod = HTTP_POST)
    @PostMapping(value = "/ddl/table/drop")
    public Result dropTable(@RequestBody @Valid DropTableArgs args) {
        log.info("METHOD [dropTable] BEGINS => {}", args);
        return tableService.dropTable(args);
    }

}
