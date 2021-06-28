package org.frank.flinksql.service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FatalExitExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final FatalExitExceptionHandler INSTANCE = new FatalExitExceptionHandler();

    @Override
    @SuppressWarnings("finally")
    public void uncaughtException(Thread t, Throwable e) {
        try {
            log.error("FATAL: Thread '" + t.getName() +
                    "' produced an uncaught exception. Stopping the process...", e);
        } finally {
            System.exit(-17);
        }
    }
}
