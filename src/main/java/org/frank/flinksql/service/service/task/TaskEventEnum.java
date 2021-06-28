package org.frank.flinksql.service.service.task;

public enum TaskEventEnum {
    CREATE(1),
    DROP(2),
    ALTER(3),
    DEPLOY(4);

    private final int type;

    TaskEventEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
