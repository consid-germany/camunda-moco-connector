package com.consid.bpm;


import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.not;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest
@ZeebeSpringTest
public class AuthorizationTest {

    public static final String VALID_API_TOKEN = "abcdef";

    @Autowired
    private ZeebeClient client;

    private String baseUrl;

    @RegisterExtension
    static WireMockExtension extension = WireMockExtension.newInstance()
            .options(wireMockConfig().port(4545))
            .build();

    @BeforeEach
    public void setup() {
        baseUrl = extension.baseUrl();
        WireMock wireMock = extension.getRuntimeInfo().getWireMock();

        MappingBuilder unauthorized = WireMock.post(urlPathMatching(".*"))
                .withHeader("Authorization", absent())
                .withHeader("Authorization", not(new EqualToPattern("Token token=" + VALID_API_TOKEN)))
                .willReturn(unauthorized());

        wireMock.register(unauthorized);
    }

    @Test
    public void test_create_absence_with_wrong_api_key_fails_as_expected() throws InterruptedException {
        // given
        String invalidApiToken = "1234567";

        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("create-absence-process")
                .latestVersion()
                .variables(
                        Map.of(
                                "baseUrl", baseUrl,
                                "apiKey", invalidApiToken,
                                "employeeMocoId", 123456,
                                "dateOfAbsence", "2024-01-01"
                        )
                )
                .send()
                .join();

        // wait for retries
        Thread.sleep(1000);

        // then
        assertThat(instance).hasAnyIncidents();
    }

}
