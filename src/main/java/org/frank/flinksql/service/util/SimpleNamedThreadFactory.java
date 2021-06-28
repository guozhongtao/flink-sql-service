package org.frank.flinksql.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleNamedThreadFactory implements ThreadFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(SimpleNamedThreadFactory.class);

    private static final LogUncaughtExceptionHandler UNCAUGHT_EX_HANDLER = new LogUncaughtExceptionHandler();

    private final String prefix;

    private final AtomicInteger counter = new AtomicInteger(0);
    private final boolean daemon;

    public SimpleNamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public SimpleNamedThreadFactory(String prefix, boolean daemon) {
        super();
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(this.daemon);
        t.setUncaughtExceptionHandler(UNCAUGHT_EX_HANDLER);
        t.setName(this.prefix + counter.getAndIncrement());
        return t;
    }

    private static final class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.error("Uncaught exception in thread {}", t, e);
        }
    }

}
