package org.frank.flinksql.service.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@Configuration
@ConfigurationProperties
@ManagedResource(objectName = "gatewayserver:name=com.tuhu.streamservice.gateway.config.GatewayConfig", description = "gateway config")
@Slf4j
public class GatewayConfig {

    @Value("${task.shell.thread.pool.core}")
    private Integer taskShellPoolCore;

    @Value("${task.shell.thread.pool.max}")
    private Integer taskShellPoolMax;

    @Value("${task.shell.thread.pool.keepAliveTime}")
    private Integer taskShellPoolKeepAliveTime;

    @Value("${task.shell.thread.pool.queue}")
    private Integer taskShellPoolQueue;

    @Value("${yarn.rmMasterAddress}")
    private String rmMasterAddress;


    @Value("${yarn.rmSlaveAddress}")
    private String rmSlaveAddress;


    @ManagedAttribute
    public Integer getTaskShellPoolCore() {
        return taskShellPoolCore;
    }

    @ManagedAttribute
    public void setTaskShellPoolCore(Integer taskShellPoolCore) {
        this.taskShellPoolCore = taskShellPoolCore;
    }

    @ManagedAttribute
    public Integer getTaskShellPoolMax() {
        return taskShellPoolMax;
    }

    @ManagedAttribute
    public void setTaskShellPoolMax(Integer taskShellPoolMax) {
        this.taskShellPoolMax = taskShellPoolMax;
    }

    @ManagedAttribute
    public Integer getTaskShellPoolKeepAliveTime() {
        return taskShellPoolKeepAliveTime;
    }

    @ManagedAttribute
    public void setTaskShellPoolKeepAliveTime(Integer taskShellPoolKeepAliveTime) {
        this.taskShellPoolKeepAliveTime = taskShellPoolKeepAliveTime;
    }

    @ManagedAttribute
    public Integer getTaskShellPoolQueue() {
        return taskShellPoolQueue;
    }

    @ManagedAttribute
    public void setTaskShellPoolQueue(Integer taskShellPoolQueue) {
        this.taskShellPoolQueue = taskShellPoolQueue;
    }

    @ManagedAttribute
    public String getRmMasterAddress() {
        return rmMasterAddress;
    }

    @ManagedAttribute
    public void setRmMasterAddress(String rmMasterAddress) {
        this.rmMasterAddress = rmMasterAddress;
    }

    @ManagedAttribute
    public String getRmSlaveAddress() {
        return rmSlaveAddress;
    }

    @ManagedAttribute
    public void setRmSlaveAddress(String rmSlaveAddress) {
        this.rmSlaveAddress = rmSlaveAddress;
    }
}
