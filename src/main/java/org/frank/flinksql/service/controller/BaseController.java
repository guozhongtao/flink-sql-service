package org.frank.flinksql.service.controller;

import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseController {

    protected static final String HTTP_GET = "GET";
    protected static final String HTTP_HEAD = "HEAD";
    protected static final String HTTP_POST = "POST";
    protected static final String HTTP_PUT = "PUT";
    protected static final String HTTP_PATCH = "PATCH";
    protected static final String HTTP_DELETE = "DELETE";
    protected static final String HTTP_OPTIONS = "OPTIONS";

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleBadRequest(HttpServletRequest request, HttpServletResponse response,
                                   MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        if (e.getBindingResult() != null && e.getBindingResult().getFieldError() != null
                && e.getBindingResult().getFieldError().getDefaultMessage() != null) {
            return new Result(ErrorCode.DATA_BIND_ERROR.getCode(),
                    e.getBindingResult().getFieldError().getDefaultMessage());
        } else {
            return new Result(ErrorCode.PARAMETER_ERROR.getCode(), e.getMessage());
        }
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleDataBindException(HttpServletRequest request, HttpServletResponse response, BindException e) {
        log.error(e.getMessage(), e);
        BindingResult result = e.getBindingResult();
        List<String> rejectFields = new ArrayList<>();
        for (ObjectError oe : result.getAllErrors()) {
            if (oe instanceof FieldError) {
                rejectFields.add(((FieldError) oe).getField());
            }
        }
        return new Result(ErrorCode.DATA_BIND_ERROR.getCode(), "rejectField:" + rejectFields);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleInvalidHttpMessage(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException e) {
        log.error("message not valid {}", e.getMessage(), e);
        return new Result(ErrorCode.MESSAGE_NOT_READABLE_ERROR.getCode(),
                ErrorCode.MESSAGE_NOT_READABLE_ERROR.getMessage());
    }

    @ExceptionHandler(GWException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleGWException(HttpServletRequest request, HttpServletResponse response, GWException e) {
        log.error(e.getMessage(), e);
        String errorMessage = e.getMessage();
        ErrorCode errorCode = e.getErrorCode();
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = (errorCode != null ? errorCode.getMessage() : "");
        }
        return new Result(errorCode != null ? errorCode.getCode() : "E0001", errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleError(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error(e.getMessage(), e);
        String codeMsg = ErrorCode.SYSTEM_ERROR.getMessage();
        return new Result(ErrorCode.SYSTEM_ERROR.getCode(),
                StringUtils.isEmpty(e.getMessage()) ? codeMsg : codeMsg + ": " + e.getMessage());
    }

}
