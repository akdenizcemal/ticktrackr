
package com.cemal.tickrackr.client;

import com.cemal.tickrackr.exception.ElancoApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class ElancoClient {

    private static final Logger log =
            LoggerFactory.getLogger(ElancoClient.class);

    private final RestClient restClient;
    private final int maxAttempts;
    private final long initialDelayMs;

    public ElancoClient(
            @Value("${elanco.base-url}") String baseUrl,
            @Value("${elanco.retry.max-attempts}") int maxAttempts,
            @Value("${elanco.retry.initial-delay-ms}") long initialDelayMs) {

        if (maxAttempts < 1) {
            throw new IllegalArgumentException(
                    "max-attempts must be at least 1"
            );
        }

        if (initialDelayMs < 0) {
            throw new IllegalArgumentException(
                    "initial-delay-ms cannot be negative"
            );
        }

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;
    }

    public List<ElancoSightingDto> fetchAll() {

        long delay = initialDelayMs;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {
                List<ElancoSightingDto> result = restClient.get()
                        .uri("/tick-sightings")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<ElancoSightingDto>>() {});

                return result == null ? List.of() : result;

            } catch (HttpClientErrorException e) {

                throw new ElancoApiException(
                        "Elanco rejected the request: " + e.getStatusCode(), e
                );

            } catch (HttpServerErrorException | ResourceAccessException e) {

                log.warn(
                        "Fetch attempt {}/{} failed: {}",
                        attempt, maxAttempts, e.getMessage()
                );

                if (attempt == maxAttempts) {
                    throw new ElancoApiException(
                            "Could not retrieve data from Elanco after "
                                    + maxAttempts + " attempts", e
                    );
                }

                sleep(delay);
                delay *= 2;

            } catch (RestClientException e) {

                throw new ElancoApiException(
                        "Unexpected error while communicating with Elanco", e
                );
            }
        }

        throw new IllegalStateException("Unexpected retry state");
    }

    private void sleep(long ms) {

        try {
            Thread.sleep(ms);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new ElancoApiException(
                    "Interrupted while waiting to retry", e
            );
        }
    }
}
