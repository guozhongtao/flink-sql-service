package org.frank.flinksql.service.util;

import com.google.common.collect.ImmutableMap;
import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.exception.ErrorCode;

public class ResultWrapper {

    public static Result successResultWrapper(Object data) {
        return new Result(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }


    public static Result successResultWrapper() {
        return new Result(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), ImmutableMap.of());
    }

    public static Result errorResultWrapper(String errorCode, String message) {
        return new Result(errorCode, message);
    }

    public static Result errorResultWrapper() {
        return new Result(ErrorCode.SHELL_TASK_ERROR.getCode(), ErrorCode.SHELL_TASK_ERROR.getMessage());
    }

    public static Result shellExecuteResult(Object o) {
        return null == o ? ResultWrapper.successResultWrapper() : ResultWrapper.errorResultWrapper(ErrorCode.SHELL_TASK_ERROR.getCode(), ErrorCode.SHELL_TASK_ERROR.getMessage());
    }
}
