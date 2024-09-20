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
public class UserEndpointTest {

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

        String requestJson = new ClassPathResource("request/deactivate_user.json")
                .getContentAsString(UTF_8);

        String responseJson = new ClassPathResource("response/deactivate_user.json")
                .getContentAsString(UTF_8);

        MappingBuilder deactivateHappyPath = WireMock.put("/users/123")
                .withHeader("Authorization", new EqualToPattern("Token token=" + VALID_API_TOKEN))
                .withHeader("Content-Type", new EqualToPattern("application/json"))
                .withRequestBody(new EqualToJsonPattern(requestJson, true, false))
                .willReturn(okJson(responseJson));

        requestJson = new ClassPathResource("request/create_user.json")
                .getContentAsString(UTF_8);

        responseJson = new ClassPathResource("response/create_user.json")
                .getContentAsString(UTF_8);

        MappingBuilder createHappyPath = WireMock.post("/users")
                .withHeader("Authorization", new EqualToPattern("Token token=" + VALID_API_TOKEN))
                .withHeader("Content-Type", new EqualToPattern("application/json"))
                .withRequestBody(new EqualToJsonPattern(requestJson, true, false))
                .willReturn(okJson(responseJson));

        wireMock.register(deactivateHappyPath);
        wireMock.register(createHappyPath);
    }

    @Test
    public void test_deactivate_user_is_mapped_as_expected() {
        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("deactivate-user-process")
                .latestVersion()
                .variables(Map.of(
                        "baseUrl", baseUrl,
                        "apiKey", VALID_API_TOKEN,
                        "employeeMocoId", 123)
                )
                .send()
                .join();

        waitForProcessInstanceCompleted(instance);

        // then
        assertThat(instance)
                .hasVariableWithValue("userId", 123)
                .isCompleted();
    }

    @Test
    public void test_create_user_is_mapped_as_expected() {
        // when
        ProcessInstanceEvent instance = client.newCreateInstanceCommand()
                .bpmnProcessId("create-user-process")
                .latestVersion()
                .variables(Map.of(
                                "baseUrl", baseUrl,
                                "apiKey", VALID_API_TOKEN,
                                "firstName", "Sarah",
                                "lastName", "Parker",
                                "mail", "sarah.parker@example.com",
                                "teamId", 1
                        )
                )
                .send()
                .join();

        waitForProcessInstanceCompleted(instance);

        // then
        assertThat(instance)
                .hasVariableWithValue("userId", 123)
                .isCompleted();
    }

}
