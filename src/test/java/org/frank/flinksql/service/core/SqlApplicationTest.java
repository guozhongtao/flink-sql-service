package org.frank.flinksql.service.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SqlApplicationTest {

    @Test
    public void testParameter(){
        List<String> param = new ArrayList<String>(){{
            add("-sql");
            add(this.getClass().getResource("/cmd_deploy_1624100515071.arg").getPath());
            add("-pPath");
            add("/Users/wangkai/apps/install/hive-2.3.8-client");
            //add(this.getClass().getResource("/").getPath());
           /* add("-checkpointEnable");
            add("true");
            add("-checkpointingMode");
            add("EXACTLY_ONCE");
            add("-checkpointInterval");
            add("60000");
            add("-checkpointTimeout");
            add("60000");*/
        }};

        SqlApplication.main(param.toArray(new String[]{}));
    }
}
