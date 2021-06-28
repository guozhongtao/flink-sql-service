package org.frank.flinksql.service.enums;

public enum YarnStateEnum {
    NEW,
    NEW_SAVING,
    SUBMITTED,
    ACCEPTED,
    RUNNING,
    FINISHED,
    FAILED,
    KILLED,
    UNKNOWN;

    public static YarnStateEnum getYarnStateEnum(String state) {
        for (YarnStateEnum stateEnum : YarnStateEnum.values()) {
            if (stateEnum.name().equals(state)) {
                return stateEnum;
            }

        }

        return UNKNOWN;
    }
}
