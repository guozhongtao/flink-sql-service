package org.frank.flinksql.service.exception;

import lombok.Getter;

public class GWException extends RuntimeException {

    private static final long serialVersionUID = 6720413735003081354L;

    @Getter
    private ErrorCode errorCode;

    public GWException(String message) {
        super(message);
    }

    public GWException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GWException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
