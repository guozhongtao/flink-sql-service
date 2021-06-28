package org.frank.flinksql.service.enums;


/**
 * @author frank
 * @Description:
 */
public enum DeployModeEnum {
    YARN_PER(1),
    YARN_SESSION(2),
    YARN_APPLICATION(3);

    private final int type;

    DeployModeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
