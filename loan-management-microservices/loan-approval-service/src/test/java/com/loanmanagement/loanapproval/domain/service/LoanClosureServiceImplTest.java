package com.loanmanagement.loanapproval.domain.service;

import com.loanmanagement.common.dto.LoanDTO;
import com.loanmanagement.common.dto.UserDetailsDTO;
import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.loanapproval.infrastructure.client.EmiServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.LoanApplicationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.NotificationServiceClient;
import com.loanmanagement.loanapproval.infrastructure.client.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanClosureServiceImplTest {

    @Mock
    private LoanApplicationServiceClient loanApplicationServiceClient;

    @Mock
    private EmiServiceClient emiServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private LoanClosureServiceImpl loanClosureService;

    private LoanDTO loanDTO;
    private UserDetailsDTO userDetailsDTO;

    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO();
        loanDTO.setId(1L);
        loanDTO.setCustomerId(10L);
        loanDTO.setStatus("DISBURSED");

        userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setFirstName("John");
        userDetailsDTO.setLastName("Doe");
        userDetailsDTO.setEmail("john.doe@test.com");
    }

    @Test
    void closeLoan_success() {
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);
        when(emiServiceClient.areAllEmisPaid(1L)).thenReturn(true);
        when(userServiceClient.getUserById(10L)).thenReturn(userDetailsDTO);

        LoanDTO result = loanClosureService.closeLoan(1L);

        assertNotNull(result);
        verify(loanApplicationServiceClient).updateLoanStatusToClosed(1L);
        verify(notificationServiceClient).sendLoanClosedNotification(
                "john.doe@test.com",
                "John Doe",
                "1"
        );
    }

    @Test
    void closeLoan_invalidStatus() {
        loanDTO.setStatus("APPROVED");

        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);

        assertThrows(BusinessException.class,
                () -> loanClosureService.closeLoan(1L));

        verify(loanApplicationServiceClient, never()).updateLoanStatusToClosed(anyLong());
    }

    @Test
    void closeLoan_emisNotPaid() {
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);
        when(emiServiceClient.areAllEmisPaid(1L)).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> loanClosureService.closeLoan(1L));

        verify(loanApplicationServiceClient, never()).updateLoanStatusToClosed(anyLong());
    }

    @Test
    void closeLoan_notificationFailure_shouldStillCloseLoan() {
        when(loanApplicationServiceClient.getLoanById(1L)).thenReturn(loanDTO);
        when(emiServiceClient.areAllEmisPaid(1L)).thenReturn(true);
        when(userServiceClient.getUserById(10L)).thenThrow(new RuntimeException("User service down"));

        LoanDTO result = loanClosureService.closeLoan(1L);

        assertNotNull(result);
        verify(loanApplicationServiceClient).updateLoanStatusToClosed(1L);
    }
}
