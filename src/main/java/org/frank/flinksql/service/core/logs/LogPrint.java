package org.frank.flinksql.service.core.logs;

import org.frank.flinksql.service.core.enums.SqlCommand;
import org.frank.flinksql.service.core.model.SqlCommandCall;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.TableEnvironment;

@Slf4j
public class LogPrint {

    public static void logPrint(SqlCommandCall sqlCommandCall) {
        if (sqlCommandCall == null) {
            throw new NullPointerException("sqlCommandCall is null");
        }
        System.out.println("\n #############" + sqlCommandCall.sqlCommand.name() + "############# \n"
                + sqlCommandCall.operands[0]);
        log.info("\n #############{}############# \n {}", sqlCommandCall.sqlCommand.name(),
                sqlCommandCall.operands[0]);
    }

    public static void queryRestPrint(TableEnvironment tEnv, SqlCommandCall sqlCommandCall) {
        if (sqlCommandCall == null) {
            throw new NullPointerException("sqlCommandCall is null");
        }
        LogPrint.logPrint(sqlCommandCall);


        if (sqlCommandCall.getSqlCommand().name().equalsIgnoreCase(SqlCommand.SELECT.name())) {
            throw new RuntimeException("目前不支持select 语法使用");
        } else {
            tEnv.executeSql(sqlCommandCall.operands[0]).print();
        }
    }

}
