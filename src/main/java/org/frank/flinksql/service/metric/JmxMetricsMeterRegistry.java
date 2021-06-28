package org.frank.flinksql.service.metric;

import io.micrometer.core.instrument.Clock;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;

public class JmxMetricsMeterRegistry {

    private static final JmxConfig CONFIG = JmxConfig.DEFAULT;
    private static final JmxMeterRegistry JMX_METER_REGISTRY = new JmxMeterRegistry(CONFIG, Clock.SYSTEM);

    public static MetricsTracker create() {
        return new MetricsTracker(JMX_METER_REGISTRY);
    }
}
