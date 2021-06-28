package org.frank.flinksql.service.exception;

import lombok.Getter;

public enum ErrorCode {

    SUCCESS("E0000", "success"),
    SYSTEM_ERROR("E0001", "system error"),
    DATA_BIND_ERROR("E0003", "data bind error"),
    PARAMETER_ERROR("E0004", "params error"),
    MESSAGE_NOT_READABLE_ERROR("E0005", "invalid json format"),
    SHELL_TASK_ERROR("E0006", "shell task failed"),
    SQL_FORMAT_ERROR("E0007", "sql format error"),
    HTTP_REQUEST_IS_NULL("E0008", "appid参数为空");


    @Getter
    private final String code;

    @Getter
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
