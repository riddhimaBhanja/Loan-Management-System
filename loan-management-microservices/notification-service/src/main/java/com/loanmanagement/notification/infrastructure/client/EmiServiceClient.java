package com.loanmanagement.notification.infrastructure.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Client for communicating with EMI Service
 */
@Component
@Slf4j
public class EmiServiceClient {

    private final RestTemplate restTemplate;
    private final String emiServiceUrl;

    public EmiServiceClient(
            RestTemplate restTemplate,
            @Value("${services.emi-service.url}") String emiServiceUrl) {
        this.restTemplate = restTemplate;
        this.emiServiceUrl = emiServiceUrl;
    }

    /**
     * Get upcoming EMIs due within next N days
     */
    public List<EmiScheduleDto> getUpcomingEmis(Integer daysAhead) {
        log.info("Fetching upcoming EMIs due within next {} days", daysAhead);

        String url = UriComponentsBuilder.fromHttpUrl(emiServiceUrl)
                .path("/api/internal/emis/upcoming")
                .queryParam("daysAhead", daysAhead)
                .toUriString();

        try {
            ResponseEntity<List<EmiScheduleDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EmiScheduleDto>>() {}
            );

            List<EmiScheduleDto> emis = response.getBody();
            log.info("Successfully fetched {} upcoming EMIs", emis != null ? emis.size() : 0);
            return emis != null ? emis : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch upcoming EMIs from EMI service. Error: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * DTO for EMI Schedule (matches EmiScheduleResponse from emi-service)
     */
    @Data
    public static class EmiScheduleDto {
        private Long id;
        private Long loanId;
        private Long customerId;
        private Integer emiNumber;
        private BigDecimal emiAmount;
        private LocalDate dueDate;
        private String status;
    }
}
