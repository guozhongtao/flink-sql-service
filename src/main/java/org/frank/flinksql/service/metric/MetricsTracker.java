package org.frank.flinksql.service.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class MetricsTracker {

    public static final String GATEWAY_METRIC_NAME_PREFIX = "gateway";
    private static final String METRIC_CATEGORY = "type";
    private static final String METRIC_SHELL_FAILED_COUNT = GATEWAY_METRIC_NAME_PREFIX + ".shell.failed";

    private final Counter shellFailedCounter;

    public MetricsTracker(final MeterRegistry meterRegistry) {
        this.shellFailedCounter = Counter.builder(METRIC_SHELL_FAILED_COUNT)
                .description("Connection timeout total count")
                .tags(METRIC_CATEGORY, "shell")
                .register(meterRegistry);
    }

    public void recordShellFailed() {
        this.shellFailedCounter.increment();
    }

}
