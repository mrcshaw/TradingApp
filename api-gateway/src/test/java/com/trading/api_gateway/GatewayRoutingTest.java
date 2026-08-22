package com.trading.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayRoutingTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testHealthRoute() {
        webClient.get().uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Gateway is healthy");
    }
}
