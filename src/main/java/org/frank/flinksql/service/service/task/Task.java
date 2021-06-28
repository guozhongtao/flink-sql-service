package org.frank.flinksql.service.service.task;

import org.frank.flinksql.service.model.param.Args;

import java.util.List;

public interface Task {
    void run(Args args, CallBack callBack);

    List<String> buildCmd();
}
