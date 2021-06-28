package org.frank.flinksql.service.core.model;

import lombok.Data;

@Data
public class JobRunParam {

    private String sqlPath;

    private CheckPointParam checkPointParam;

    private String projectPath;

    private String zkAddress;

    private String storageDir;

    private String appName;

}
