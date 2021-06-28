package org.frank.flinksql.service.core.execute;


import org.frank.flinksql.service.core.config.Configurations;
import org.frank.flinksql.service.core.logs.LogPrint;
import org.frank.flinksql.service.core.model.SqlCommandCall;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;

import java.util.List;

public class ExecuteSql {

    public static void exeSql(List<SqlCommandCall> sqlCommandCallList, TableEnvironment tEnv, StatementSet statementSet) {
        for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {
            switch (sqlCommandCall.sqlCommand) {
                //配置
                case SET:
                    Configurations.setConfigurationString(tEnv, sqlCommandCall.operands[0],
                            sqlCommandCall.operands[1]);
                    break;
                //insert 语句
                case INSERT_INTO:
                case INSERT_OVERWRITE:
                    LogPrint.logPrint(sqlCommandCall);
                    statementSet.addInsertSql(sqlCommandCall.operands[0]);
                    break;
                //显示语句
                case SELECT:
                case SHOW_CATALOGS:
                case SHOW_DATABASES:
                case SHOW_MODULES:
                case SHOW_TABLES:
                    LogPrint.queryRestPrint(tEnv, sqlCommandCall);
                    break;
                default:
                    LogPrint.logPrint(sqlCommandCall);
                    tEnv.executeSql(sqlCommandCall.operands[0]);
                    break;
            }
        }
    }
}
