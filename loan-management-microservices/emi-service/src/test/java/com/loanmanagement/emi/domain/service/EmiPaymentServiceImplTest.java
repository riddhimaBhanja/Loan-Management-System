package com.loanmanagement.emi.domain.service;

import com.loanmanagement.common.exception.BusinessException;
import com.loanmanagement.common.exception.ResourceNotFoundException;
import com.loanmanagement.emi.application.dto.request.EmiPaymentRequest;
import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;
import com.loanmanagement.emi.application.mapper.EmiPaymentMapper;
import com.loanmanagement.emi.domain.model.EmiPayment;
import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import com.loanmanagement.emi.domain.model.PaymentMethod;
import com.loanmanagement.emi.domain.repository.EmiPaymentRepository;
import com.loanmanagement.emi.domain.repository.EmiScheduleRepository;
import com.loanmanagement.emi.infrastructure.client.UserServiceClient;
import com.loanmanagement.emi.shared.constants.MessageConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmiPaymentServiceImplTest {

    @InjectMocks
    private EmiPaymentServiceImpl service;

    @Mock
    private EmiPaymentRepository emiPaymentRepository;

    @Mock
    private EmiScheduleRepository emiScheduleRepository;

    @Mock
    private EmiPaymentMapper emiPaymentMapper;

    @Mock
    private UserServiceClient userServiceClient;

    private EmiSchedule emiSchedule;
    private EmiPayment payment;
    private EmiPaymentRequest request;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user1", null)
        );

        emiSchedule = EmiSchedule.builder()
                .id(1L)
                .loanId(100L)
                .emiNumber(1)
                .emiAmount(new BigDecimal("5000"))
                .status(EmiStatus.PENDING)
                .build();

        request = EmiPaymentRequest.builder()
                .emiScheduleId(1L)
                .amount(new BigDecimal("5000"))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.UPI)
                .transactionReference("TXN123")
                .remarks("Test payment")
                .build();

        payment = EmiPayment.builder()
                .id(10L)
                .emiScheduleId(1L)
                .loanId(100L)
                .amount(new BigDecimal("5000"))
                .paidBy(1L)
                .build();
    }

    @Test
    void recordPayment_success_fullPayment() {
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.of(emiSchedule));
        when(emiPaymentRepository.save(any())).thenReturn(payment);
        when(emiPaymentMapper.toResponse(payment)).thenReturn(new EmiPaymentResponse());
        when(userServiceClient.getUserIdByUsername(any())).thenReturn(1L);
        when(userServiceClient.getUserName(1L)).thenReturn("Test User");

        EmiPaymentResponse response = service.recordPayment(request);

        assertNotNull(response);
        verify(emiScheduleRepository).save(emiSchedule);
        verify(emiPaymentRepository).save(any());
    }

    @Test
    void recordPayment_emiNotFound() {
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.recordPayment(request)
        );

        assertEquals(MessageConstants.EMI_NOT_FOUND, ex.getMessage());
    }

    @Test
    void recordPayment_alreadyPaid() {
        emiSchedule.setStatus(EmiStatus.PAID);
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.of(emiSchedule));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.recordPayment(request)
        );

        assertEquals(MessageConstants.EMI_ALREADY_PAID, ex.getMessage());
    }

    @Test
    void recordPayment_invalidAmount() {
        request.setAmount(BigDecimal.ZERO);
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.of(emiSchedule));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.recordPayment(request)
        );

        assertEquals(MessageConstants.INVALID_PAYMENT_AMOUNT, ex.getMessage());
    }

    @Test
    void recordPayment_duplicateTransactionReference() {
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.of(emiSchedule));
        when(emiPaymentRepository.findByTransactionReference("TXN123"))
                .thenReturn(Optional.of(payment));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.recordPayment(request)
        );

        assertEquals(MessageConstants.DUPLICATE_TRANSACTION_REFERENCE, ex.getMessage());
    }

    @Test
    void getPaymentHistory_success() {
        when(emiPaymentRepository.findByLoanIdOrderByPaymentDateDesc(100L))
                .thenReturn(List.of(payment));
        when(emiPaymentMapper.toResponseList(any()))
                .thenReturn(List.of(new EmiPaymentResponse()));
        when(emiScheduleRepository.findById(1L))
                .thenReturn(Optional.of(emiSchedule));

        List<EmiPaymentResponse> responses = service.getPaymentHistory(100L);

        assertEquals(1, responses.size());
    }

    @Test
    void getPaymentById_success() {
        when(emiPaymentRepository.findById(10L)).thenReturn(Optional.of(payment));
        when(emiPaymentMapper.toResponse(payment)).thenReturn(new EmiPaymentResponse());
        when(emiScheduleRepository.findById(1L)).thenReturn(Optional.of(emiSchedule));
        when(userServiceClient.getUserName(any())).thenReturn("Test User");

        EmiPaymentResponse response = service.getPaymentById(10L);

        assertNotNull(response);
    }

    @Test
    void getPaymentById_notFound() {
        when(emiPaymentRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getPaymentById(10L)
        );

        assertEquals(MessageConstants.PAYMENT_NOT_FOUND, ex.getMessage());
    }

    @Test
    void getPaymentByTransactionReference_success() {
        when(emiPaymentRepository.findByTransactionReference("TXN123"))
                .thenReturn(Optional.of(payment));
        when(emiPaymentMapper.toResponse(payment))
                .thenReturn(new EmiPaymentResponse());
        when(emiScheduleRepository.findById(1L))
                .thenReturn(Optional.of(emiSchedule));

        EmiPaymentResponse response =
                service.getPaymentByTransactionReference("TXN123");

        assertNotNull(response);
    }
}
