package org.frank.flinksql.service.util;

import org.frank.flinksql.service.exception.GWException;
import lombok.extern.slf4j.Slf4j;
import org.frank.flinksql.service.exception.ErrorCode;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ShellCmd {

    private static final long SCRIPT_TIMEOUT_MS = 10000;

    public static List<String> runCommand(List<String> commands, final String directory) {
        log.info("run cmd , commands is {}, directory is {}", commands, directory);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        File file = new File(directory);
        processBuilder.directory(file);  //切换到工作目录
        //processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (Exception e) {
            log.error("run shell cmd failed, {}", e.getMessage(), e);
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        }

        BufferedReader br = null;
        BufferedReader errorbr = null;
        try {
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));//得到命令执行的流
            errorbr = new BufferedReader(new InputStreamReader(process.getErrorStream()));//得到命令执行的错误流
            StringBuilder result = new StringBuilder();
            StringBuilder errorresult = new StringBuilder();
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                result.append(lineStr).append("\n");
            }

            log.info("result: {}" + result);
            while ((lineStr = errorbr.readLine()) != null) {
                errorresult.append(lineStr);
            }

            log.error("errorresult: {}" + errorresult);

            final int status = process.waitFor(); //阻塞，直到上述命令执行完
            log.info("run cmd status {}", status);

            if (result.toString().contains("[ERROR]")){
                throw new GWException(result.substring(result.indexOf("[ERROR]")));
            }
            return Arrays.asList(result.toString(), errorresult.toString());
        } catch (Exception e) {
            log.error("run shell cmd failed, {}", e.getMessage(), e);
            throw new GWException(e.getMessage(), ErrorCode.SHELL_TASK_ERROR);
        } finally {
            process.destroyForcibly();
            if (br !=null){
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("close stream failed {}", e);
                }
            }
            if (null != errorbr){
                try {
                    errorbr.close();
                } catch (IOException e) {
                    log.error("close stream failed {}", e);
                }
            }
            log.info("执行结束 {}", commands);
        }
    }


    public static String executeScript(File discoveryScript, String args) throws Exception {
        final String cmd = discoveryScript.getAbsolutePath() + " " + args;
        final Process process = Runtime.getRuntime().exec(cmd);
        try (final BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             final BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            final boolean hasProcessTerminated = process.waitFor(SCRIPT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!hasProcessTerminated) {
                throw new TimeoutException(String.format("The script executed for over %d ms.", SCRIPT_TIMEOUT_MS));
            }

            final int exitVal = process.exitValue();
            if (exitVal != 0) {
                final String stdout = stdoutReader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
                final String stderr = stderrReader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
                log.warn("script exit with {}.\nSTDOUT: {}\nSTDERR: {}", exitVal, stdout, stderr);
                throw new GWException(String.format("script exit with non-zero return code: %s.", exitVal));
            }
            Object[] stdout = stdoutReader.lines().toArray();
            if (stdout.length > 1) {
                log.warn(
                        "The output of the script should only contain one single line. Finding {} lines with content: {}. Will only keep the first line.", stdout.length, Arrays.toString(stdout));
            }
            if (stdout.length == 0) {
                return "";
            }
            return (String) stdout[0];
        } finally {
            process.destroyForcibly();
        }
    }


}
