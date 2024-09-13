package com.consid.bpm;

import io.camunda.connector.runtime.InboundConnectorsAutoConfiguration;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {InboundConnectorsAutoConfiguration.class})
@Deployment(resources = "bpmn/example.bpmn")
public class LocalConnectorRuntime {

    public static void main(String[] args) {
        SpringApplication.run(LocalConnectorRuntime.class);
    }

}
