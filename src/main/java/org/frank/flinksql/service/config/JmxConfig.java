package org.frank.flinksql.service.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

@EnableMBeanExport
@Slf4j
//@Configuration
public class JmxConfig {

    @Value("${gatewayserver.jmx.host}")
    private String jmxHost;

    @Value("${gatewayserver.jmx.port}")
    private Integer jmxPort;

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
//        rmiRegistryFactoryBean.setHost(jmxHost);
        rmiRegistryFactoryBean.setPort(jmxPort);
        rmiRegistryFactoryBean.setAlwaysCreate(true);

        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        String serviceUrl = String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", jmxHost, jmxPort, jmxHost, jmxPort);
        connectorServerFactoryBean.setServiceUrl(serviceUrl);
        log.info("ConnectorServerFactoryBean create success !! url:{}", serviceUrl);

        return connectorServerFactoryBean;
    }

}
