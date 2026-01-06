package com.loanmanagement.loanapproval.infrastructure.client;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LoanApplicationServiceClient loanApplicationServiceClient;

    private final String loanServiceUrl = "http://loan-service";

    @BeforeEach
    void setUp() {
        loanApplicationServiceClient =
                new LoanApplicationServiceClient(restTemplate, loanServiceUrl);
    }

    @Test
    void getLoanById_success() {
        Long loanId = 1L;
        LoanDTO loanDTO = new LoanDTO();

        ResponseEntity<LoanDTO> response =
                new ResponseEntity<>(loanDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(
                loanServiceUrl + "/api/internal/loans/" + loanId,
                LoanDTO.class
        )).thenReturn(response);

        LoanDTO result = loanApplicationServiceClient.getLoanById(loanId);

        assertNotNull(result);
        assertEquals(loanDTO, result);
    }

    @Test
    void getLoanById_notFound_throwsException() {
        Long loanId = 2L;

        when(restTemplate.getForEntity(anyString(), eq(LoanDTO.class)))
                .thenThrow(new RuntimeException("404"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> loanApplicationServiceClient.getLoanById(loanId)
        );
    }

    @Test
    void updateLoanStatusToApproved_success() {
        Long loanId = 1L;
        BigDecimal approvedAmount = BigDecimal.valueOf(50000);
        BigDecimal interestRate = BigDecimal.valueOf(9);

        doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class));

        assertDoesNotThrow(() ->
                loanApplicationServiceClient.updateLoanStatusToApproved(
                        loanId, approvedAmount, interestRate
                )
        );

        ArgumentCaptor<HttpEntity<String>> captor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).put(
                eq(loanServiceUrl + "/api/internal/loans/" + loanId + "/approve"),
                captor.capture()
        );

        assertEquals(MediaType.APPLICATION_JSON,
                captor.getValue().getHeaders().getContentType());
    }

    @Test
    void updateLoanStatusToApproved_failure() {
        doThrow(new RuntimeException("error"))
                .when(restTemplate).put(anyString(), any());

        assertThrows(
                RuntimeException.class,
                () -> loanApplicationServiceClient.updateLoanStatusToApproved(
                        1L, BigDecimal.TEN, BigDecimal.ONE
                )
        );
    }

    @Test
    void updateLoanStatusToRejected_success() {
        Long loanId = 3L;
        String reason = "Invalid documents";

        doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class));

        assertDoesNotThrow(() ->
                loanApplicationServiceClient.updateLoanStatusToRejected(
                        loanId, reason
                )
        );
    }

    @Test
    void updateLoanStatusToDisbursed_success() {
        Long loanId = 4L;
        LocalDate disbursementDate = LocalDate.now();
        String method = "BANK_TRANSFER";
        String reference = "REF123";

        doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class));

        assertDoesNotThrow(() ->
                loanApplicationServiceClient.updateLoanStatusToDisbursed(
                        loanId, disbursementDate, method, reference
                )
        );
    }

    @Test
    void updateLoanStatusToClosed_success() {
        Long loanId = 5L;

        doNothing().when(restTemplate).put(anyString(), isNull());

        assertDoesNotThrow(() ->
                loanApplicationServiceClient.updateLoanStatusToClosed(loanId)
        );
    }

    @Test
    void getAllLoans_success() {
        LoanDTO[] loans = { new LoanDTO(), new LoanDTO() };

        ResponseEntity<LoanDTO[]> response =
                new ResponseEntity<>(loans, HttpStatus.OK);

        when(restTemplate.getForEntity(
                loanServiceUrl + "/api/internal/loans",
                LoanDTO[].class
        )).thenReturn(response);

        List<LoanDTO> result = loanApplicationServiceClient.getAllLoans();

        assertEquals(2, result.size());
    }

    @Test
    void getAllLoans_exception_returnsEmptyList() {
        when(restTemplate.getForEntity(anyString(), eq(LoanDTO[].class)))
                .thenThrow(new RuntimeException());

        List<LoanDTO> result = loanApplicationServiceClient.getAllLoans();

        assertTrue(result.isEmpty());
    }

    @Test
    void getLoansByOfficerId_success() {
        Long officerId = 10L;
        LoanDTO[] loans = { new LoanDTO() };

        ResponseEntity<LoanDTO[]> response =
                new ResponseEntity<>(loans, HttpStatus.OK);

        when(restTemplate.getForEntity(
                loanServiceUrl + "/api/internal/loans/officer/" + officerId,
                LoanDTO[].class
        )).thenReturn(response);

        List<LoanDTO> result =
                loanApplicationServiceClient.getLoansByOfficerId(officerId);

        assertEquals(1, result.size());
    }

    @Test
    void getLoansByOfficerId_exception_returnsEmptyList() {
        when(restTemplate.getForEntity(anyString(), eq(LoanDTO[].class)))
                .thenThrow(new RuntimeException());

        List<LoanDTO> result =
                loanApplicationServiceClient.getLoansByOfficerId(99L);

        assertTrue(result.isEmpty());
    }
}
