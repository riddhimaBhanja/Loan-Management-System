package com.loanmanagement.emi.domain.repository;

import com.loanmanagement.emi.domain.model.EmiPayment;
import com.loanmanagement.emi.domain.model.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class EmiPaymentRepositoryTest {

    @Autowired
    private EmiPaymentRepository emiPaymentRepository;

    private EmiPayment createPayment(
            Long loanId,
            Long emiScheduleId,
            BigDecimal amount,
            LocalDate paymentDate,
            String txnRef,
            Long paidBy
    ) {
        return EmiPayment.builder()
                .loanId(loanId)
                .emiScheduleId(emiScheduleId)
                .amount(amount)
                .paymentDate(paymentDate)
                .paymentMethod(PaymentMethod.UPI)
                .transactionReference(txnRef)
                .paidBy(paidBy)
                .remarks("test")
                .build();
    }

    @Test
    void shouldFindByLoanIdOrderByPaymentDateDesc() {
        emiPaymentRepository.save(
                createPayment(1L, 10L, BigDecimal.valueOf(1000), LocalDate.now().minusDays(1), "TXN1", 1L)
        );
        emiPaymentRepository.save(
                createPayment(1L, 11L, BigDecimal.valueOf(2000), LocalDate.now(), "TXN2", 1L)
        );

        List<EmiPayment> payments =
                emiPaymentRepository.findByLoanIdOrderByPaymentDateDesc(1L);

        assertEquals(2, payments.size());
        assertTrue(payments.get(0).getPaymentDate()
                .isAfter(payments.get(1).getPaymentDate()));
    }

    @Test
    void shouldFindByLoanIdWithPagination() {
        emiPaymentRepository.save(
                createPayment(2L, 20L, BigDecimal.valueOf(1000), LocalDate.now(), "TXN3", 1L)
        );

        Page<EmiPayment> page =
                emiPaymentRepository.findByLoanId(2L, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void shouldFindByEmiScheduleId() {
        emiPaymentRepository.save(
                createPayment(3L, 30L, BigDecimal.valueOf(1500), LocalDate.now(), "TXN4", 2L)
        );

        Optional<EmiPayment> payment =
                emiPaymentRepository.findByEmiScheduleId(30L);

        assertTrue(payment.isPresent());
        assertEquals(BigDecimal.valueOf(1500), payment.get().getAmount());
    }

    @Test
    void shouldFindAllByEmiScheduleId() {
        emiPaymentRepository.save(
                createPayment(4L, 40L, BigDecimal.valueOf(1000), LocalDate.now(), "TXN5", 3L)
        );
        emiPaymentRepository.save(
                createPayment(4L, 40L, BigDecimal.valueOf(2000), LocalDate.now(), "TXN6", 3L)
        );

        List<EmiPayment> payments =
                emiPaymentRepository.findAllByEmiScheduleId(40L);

        assertEquals(2, payments.size());
    }

    @Test
    void shouldFindPaymentsBetweenDates() {
        emiPaymentRepository.save(
                createPayment(5L, 50L, BigDecimal.valueOf(1000), LocalDate.now().minusDays(5), "TXN7", 4L)
        );
        emiPaymentRepository.save(
                createPayment(5L, 51L, BigDecimal.valueOf(2000), LocalDate.now().minusDays(1), "TXN8", 4L)
        );

        List<EmiPayment> payments =
                emiPaymentRepository.findPaymentsBetweenDates(
                        LocalDate.now().minusDays(7),
                        LocalDate.now()
                );

        assertEquals(2, payments.size());
    }

    @Test
    void shouldFindByTransactionReference() {
        emiPaymentRepository.save(
                createPayment(6L, 60L, BigDecimal.valueOf(3000), LocalDate.now(), "UNIQUE_TXN", 5L)
        );

        Optional<EmiPayment> payment =
                emiPaymentRepository.findByTransactionReference("UNIQUE_TXN");

        assertTrue(payment.isPresent());
    }

    @Test
    void shouldGetTotalPaymentsForLoan() {
        emiPaymentRepository.save(
                createPayment(7L, 70L, BigDecimal.valueOf(1000), LocalDate.now(), "TXN9", 6L)
        );
        emiPaymentRepository.save(
                createPayment(7L, 71L, BigDecimal.valueOf(2000), LocalDate.now(), "TXN10", 6L)
        );

        Optional<BigDecimal> total =
                emiPaymentRepository.getTotalPaymentsForLoan(7L);

        assertTrue(total.isPresent());
        assertEquals(0, BigDecimal.valueOf(3000).compareTo(total.get()));
    }

    @Test
    void shouldCountByLoanId() {
        emiPaymentRepository.save(
                createPayment(8L, 80L, BigDecimal.valueOf(1000), LocalDate.now(), "TXN11", 7L)
        );
        emiPaymentRepository.save(
                createPayment(8L, 81L, BigDecimal.valueOf(1000), LocalDate.now(), "TXN12", 7L)
        );

        Long count = emiPaymentRepository.countByLoanId(8L);

        assertEquals(2L, count);
    }

    @Test
    void shouldFindRecentPayments() {
        emiPaymentRepository.save(
                createPayment(9L, 90L, BigDecimal.valueOf(500), LocalDate.now().minusDays(2), "TXN13", 8L)
        );

        List<EmiPayment> payments =
                emiPaymentRepository.findRecentPayments(LocalDate.now().minusDays(3));

        assertEquals(1, payments.size());
    }

    @Test
    void shouldFindByPaidByWithPagination() {
        emiPaymentRepository.save(
                createPayment(10L, 100L, BigDecimal.valueOf(1200), LocalDate.now(), "TXN14", 99L)
        );

        Page<EmiPayment> page =
                emiPaymentRepository.findByPaidBy(99L, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void shouldGetTotalPaidForEmi() {
        emiPaymentRepository.save(
                createPayment(11L, 110L, BigDecimal.valueOf(700), LocalDate.now(), "TXN15", 10L)
        );
        emiPaymentRepository.save(
                createPayment(11L, 110L, BigDecimal.valueOf(1300), LocalDate.now(), "TXN16", 10L)
        );

        Optional<BigDecimal> total =
                emiPaymentRepository.getTotalPaidForEmi(110L);

        assertTrue(total.isPresent());
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(total.get()));
    }
}
