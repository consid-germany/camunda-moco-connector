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
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.not;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;

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

        String requestJson = new ClassPathResource("request/create_absence_request.json")
                .getContentAsString(StandardCharsets.UTF_8);

        String responseJson = new ClassPathResource("response/create_absence_response.json")
                .getContentAsString(StandardCharsets.UTF_8);

        MappingBuilder unauthorized = WireMock.post(urlPathMatching(".*"))
                .withHeader("Authorization", absent())
                .withHeader("Authorization", not(new EqualToPattern("Token token=" + VALID_API_TOKEN)))
                .willReturn(unauthorized());

        MappingBuilder happyPath = WireMock.post("/schedules")
                .withHeader("Authorization", new EqualToPattern("Token token=" + VALID_API_TOKEN))
                .withHeader("Content-Type", new EqualToPattern("application/json"))
                .withRequestBody(new EqualToJsonPattern(requestJson, true, false))
                .willReturn(okJson(responseJson));

        wireMock.register(happyPath);
        wireMock.register(unauthorized);
    }

    @Test
    public void test_create_absence_is_mapped_as_expected() {
        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("example")
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

    @Test
    public void test_create_absence_with_wrong_api_key_fails_as_expected() throws InterruptedException {
        // given
        String invalidApiToken = "1234567";

        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("example")
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
