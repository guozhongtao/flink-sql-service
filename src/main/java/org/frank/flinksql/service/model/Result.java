package org.frank.flinksql.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.frank.flinksql.service.exception.ErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 4461166416713067736L;

    @Getter
    private long code;

    @Getter
    private String errorCode;

    @Getter
    private String message;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    private Object data;

    public Result() {
        super();
    }

    public Result(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.message = errorMessage;
        this.code = ErrorCode.SUCCESS.getCode().equals(errorCode) ? 0 : 1;
    }

    public Result(ErrorCode err) {
        this(err.getCode(), err.getMessage());
    }

    public Result(String errorCode, String errorMessage, Object data) {
        this(errorCode, errorMessage);
        this.data = data;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
