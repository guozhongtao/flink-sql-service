package org.frank.flinksql.service.util;

import org.junit.Test;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShellCmdTest {

    @Test
    public void test() {
        List<String> commands = new ArrayList<>();
        commands.add("sh");
        commands.add("/tmp/cmd_create_1623049539080.sh");
        ShellCmd shellCmd = new ShellCmd();
        shellCmd.runCommand(commands, "/tmp");
    }

    @Test
    public void testPath() {
        String path3 = System.getProperty("user.dir");
        System.out.println("path3：" + path3);

        ApplicationHome h = new ApplicationHome(getClass());
        File jarFile = h.getSource();
        System.out.println("test Path " + jarFile.getAbsolutePath());
    }
}
