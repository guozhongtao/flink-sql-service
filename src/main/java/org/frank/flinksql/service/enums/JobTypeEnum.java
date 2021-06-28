package org.frank.flinksql.service.enums;

import lombok.Getter;

/**
 * @author frank
 * @Description:
 */
@Getter
public enum JobTypeEnum {

    SQL(0), JAR(1);

    private int code;

    JobTypeEnum(int code) {
        this.code = code;
    }

    public static JobTypeEnum getJobTypeEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (JobTypeEnum jobTypeEnum : JobTypeEnum.values()) {
            if (code == jobTypeEnum.getCode()) {
                return jobTypeEnum;
            }
        }

        return null;
    }
}
