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

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest
@ZeebeSpringTest
public class ScheduleEndpointTest {

    public static final String VALID_API_TOKEN = "abcdef";

    @Autowired
    private ZeebeClient client;

    private String baseUrl;

    @RegisterExtension
    static WireMockExtension extension = WireMockExtension.newInstance()
            .options(wireMockConfig().port(4545))
            .build();

    @BeforeEach
    public void setup() throws IOException {
        baseUrl = extension.baseUrl();
        WireMock wireMock = extension.getRuntimeInfo().getWireMock();

        String requestJson = new ClassPathResource("request/create_absence.json")
                .getContentAsString(UTF_8);

        String responseJson = new ClassPathResource("response/create_absence.json")
                .getContentAsString(UTF_8);

        MappingBuilder happyPath = WireMock.post("/schedules")
                .withHeader("Authorization", new EqualToPattern("Token token=" + VALID_API_TOKEN))
                .withHeader("Content-Type", new EqualToPattern("application/json"))
                .withRequestBody(new EqualToJsonPattern(requestJson, true, false))
                .willReturn(okJson(responseJson));

        wireMock.register(happyPath);
    }

    @Test
    public void test_create_absence_is_mapped_as_expected() {
        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("create-absence-process")
                .latestVersion()
                .variables(Map.of(
                        "baseUrl", baseUrl,
                        "apiKey", VALID_API_TOKEN,
                        "employeeMocoId", 123456,
                        "dateOfAbsence", "2024-01-01")
                )
                .send()
                .join();

        waitForProcessInstanceCompleted(instance);

        // then
        assertThat(instance)
                .hasVariableWithValue("scheduleId", 1234567890)
                .isCompleted();
    }

}
