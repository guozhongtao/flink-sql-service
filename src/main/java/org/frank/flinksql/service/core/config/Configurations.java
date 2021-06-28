package org.frank.flinksql.service.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableEnvironment;

@Slf4j
public class Configurations {

    public static void setConfigurationString(TableEnvironment tEnv, String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        Configuration configuration = tEnv.getConfig().getConfiguration();
        log.info("#############setConfiguration#############\n  key={} value={}", key, value);
        configuration.setString(key, value);

    }

    public static void setConfigurationInt(TableEnvironment tEnv, String key, Integer value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        Configuration configuration = tEnv.getConfig().getConfiguration();
        log.info("#############setConfiguration#############\n  key={} value={}", key, value);
        configuration.setInteger(key, value);

    }


}
