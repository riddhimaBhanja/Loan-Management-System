package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.GenerateEmiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmiServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmiServiceClient emiServiceClient;

    private final String emiServiceUrl = "http://emi-service";

    @BeforeEach
    void setUp() {
        emiServiceClient = new EmiServiceClient(restTemplate, emiServiceUrl);
    }

    @Test
    void generateEmiSchedule_success() {
        Long loanId = 1L;
        Long customerId = 10L;
        BigDecimal principal = BigDecimal.valueOf(100000);
        BigDecimal interestRate = BigDecimal.valueOf(10);
        Integer tenureMonths = 12;
        LocalDate disbursementDate = LocalDate.now();

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(null);

        assertDoesNotThrow(() ->
                emiServiceClient.generateEmiSchedule(
                        loanId,
                        customerId,
                        principal,
                        interestRate,
                        tenureMonths,
                        disbursementDate
                )
        );

        ArgumentCaptor<HttpEntity<GenerateEmiRequest>> captor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).postForEntity(
                eq(emiServiceUrl + "/api/internal/emis/generate"),
                captor.capture(),
                eq(Void.class)
        );

        GenerateEmiRequest request = captor.getValue().getBody();
        assertNotNull(request);
        assertEquals(loanId, request.getLoanId());
        assertEquals(customerId, request.getCustomerId());
        assertEquals(principal, request.getPrincipal());
        assertEquals(interestRate, request.getInterestRate());
        assertEquals(tenureMonths, request.getTenureMonths());
        assertEquals(disbursementDate, request.getStartDate());
        assertEquals(MediaType.APPLICATION_JSON, captor.getValue().getHeaders().getContentType());
    }

    @Test
    void generateEmiSchedule_failure_throwsRuntimeException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RuntimeException("Service error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> emiServiceClient.generateEmiSchedule(
                        1L,
                        2L,
                        BigDecimal.TEN,
                        BigDecimal.ONE,
                        12,
                        LocalDate.now()
                )
        );

        assertTrue(ex.getMessage().contains("Failed to generate EMI schedule"));
    }

    @Test
    void areAllEmisPaid_returnsTrue() {
        Long loanId = 5L;

        when(restTemplate.getForObject(
                emiServiceUrl + "/api/internal/emis/loan/" + loanId + "/all-paid",
                Boolean.class
        )).thenReturn(true);

        boolean result = emiServiceClient.areAllEmisPaid(loanId);

        assertTrue(result);
    }

    @Test
    void areAllEmisPaid_returnsFalse_whenServiceReturnsFalse() {
        Long loanId = 6L;

        when(restTemplate.getForObject(
                emiServiceUrl + "/api/internal/emis/loan/" + loanId + "/all-paid",
                Boolean.class
        )).thenReturn(false);

        boolean result = emiServiceClient.areAllEmisPaid(loanId);

        assertFalse(result);
    }

    @Test
    void areAllEmisPaid_returnsFalse_onException() {
        Long loanId = 7L;

        when(restTemplate.getForObject(anyString(), eq(Boolean.class)))
                .thenThrow(new RuntimeException("Service down"));

        boolean result = emiServiceClient.areAllEmisPaid(loanId);

        assertFalse(result);
    }
}
