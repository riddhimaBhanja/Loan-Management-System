package com.loanmanagement.emi.application.mapper;

import com.loanmanagement.emi.application.dto.response.EmiPaymentResponse;
import com.loanmanagement.emi.domain.model.EmiPayment;
import com.loanmanagement.emi.domain.model.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmiPaymentMapperTest {

    private final EmiPaymentMapper mapper =
            Mappers.getMapper(EmiPaymentMapper.class);

    @Test
    void shouldMapEmiPaymentToResponse() {
        EmiPayment payment = EmiPayment.builder()
                .id(1L)
                .emiScheduleId(10L)
                .loanId(100L)
                .amount(BigDecimal.valueOf(5000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .transactionReference("TXN123")
                .paidBy(20L)
                .remarks("Paid successfully")
                .createdAt(LocalDateTime.now())
                .build();

        EmiPaymentResponse response = mapper.toResponse(payment);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getEmiScheduleId());
        assertEquals(100L, response.getLoanId());
        assertEquals(BigDecimal.valueOf(5000), response.getAmount());
        assertEquals(payment.getPaymentDate(), response.getPaymentDate());
        assertEquals(PaymentMethod.CASH, response.getPaymentMethod());
        assertEquals("TXN123", response.getTransactionReference());
        assertEquals(20L, response.getPaidBy());
        assertEquals("Paid successfully", response.getRemarks());
        assertEquals(payment.getCreatedAt(), response.getCreatedAt());

        // ignored fields
        assertNull(response.getEmiNumber());
        assertNull(response.getPaidByName());
    }

    @Test
    void shouldMapEmiPaymentListToResponseList() {
        EmiPayment payment1 = EmiPayment.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(3000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.UPI)
                .build();

        EmiPayment payment2 = EmiPayment.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(4000))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.DEBIT_CARD)
                .build();

        List<EmiPaymentResponse> responses =
                mapper.toResponseList(List.of(payment1, payment2));

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals(BigDecimal.valueOf(3000), responses.get(0).getAmount());

        assertEquals(2L, responses.get(1).getId());
        assertEquals(BigDecimal.valueOf(4000), responses.get(1).getAmount());
    }
}
