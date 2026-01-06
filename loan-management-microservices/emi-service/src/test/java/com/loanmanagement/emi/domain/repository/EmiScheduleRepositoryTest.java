package com.loanmanagement.emi.domain.repository;

import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmiScheduleRepositoryTest {

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;

    private EmiSchedule createEmi(
            Long loanId,
            Long customerId,
            Integer emiNumber,
            LocalDate dueDate,
            EmiStatus status
    ) {
        return EmiSchedule.builder()
                .loanId(loanId)
                .customerId(customerId)
                .emiNumber(emiNumber)
                .emiAmount(BigDecimal.valueOf(1000))
                .principalComponent(BigDecimal.valueOf(800))
                .interestComponent(BigDecimal.valueOf(200))
                .dueDate(dueDate)
                .outstandingBalance(BigDecimal.valueOf(5000))
                .status(status)
                .build();
    }

    @Test
    void shouldFindByLoanIdOrderByEmiNumberAsc() {
        emiScheduleRepository.save(createEmi(1L, 10L, 2, LocalDate.now(), EmiStatus.PENDING));
        emiScheduleRepository.save(createEmi(1L, 10L, 1, LocalDate.now(), EmiStatus.PENDING));

        List<EmiSchedule> result =
                emiScheduleRepository.findByLoanIdOrderByEmiNumberAsc(1L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getEmiNumber());
    }

    @Test
    void shouldFindByCustomerIdOrderByDueDateAsc() {
        emiScheduleRepository.save(createEmi(2L, 20L, 1, LocalDate.now().plusDays(5), EmiStatus.PENDING));
        emiScheduleRepository.save(createEmi(2L, 20L, 2, LocalDate.now().plusDays(1), EmiStatus.PENDING));

        List<EmiSchedule> result =
                emiScheduleRepository.findByCustomerIdOrderByDueDateAsc(20L);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getDueDate().isBefore(result.get(1).getDueDate()));
    }

    @Test
    void shouldFindByLoanIdAndEmiNumber() {
        emiScheduleRepository.save(createEmi(3L, 30L, 1, LocalDate.now(), EmiStatus.PENDING));

        Optional<EmiSchedule> result =
                emiScheduleRepository.findByLoanIdAndEmiNumber(3L, 1);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldFindByStatus() {
        emiScheduleRepository.save(createEmi(4L, 40L, 1, LocalDate.now(), EmiStatus.PAID));
        emiScheduleRepository.save(createEmi(4L, 40L, 2, LocalDate.now(), EmiStatus.PAID));

        List<EmiSchedule> result =
                emiScheduleRepository.findByStatus(EmiStatus.PAID);

        assertEquals(2, result.size());
    }

    @Test
    void shouldCountByLoanIdAndStatus() {
        emiScheduleRepository.save(createEmi(5L, 50L, 1, LocalDate.now(), EmiStatus.PAID));
        emiScheduleRepository.save(createEmi(5L, 50L, 2, LocalDate.now(), EmiStatus.PENDING));

        Long count =
                emiScheduleRepository.countByLoanIdAndStatus(5L, EmiStatus.PAID);

        assertEquals(1L, count);
    }

    @Test
    void shouldFindOverdueEmis() {
        emiScheduleRepository.save(createEmi(6L, 60L, 1, LocalDate.now().minusDays(2), EmiStatus.PENDING));

        List<EmiSchedule> result =
                emiScheduleRepository.findOverdueEmis(LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindUpcomingEmis() {
        emiScheduleRepository.save(createEmi(
                7L, 70L, 1, LocalDate.now().plusDays(3), EmiStatus.PENDING));

        List<EmiSchedule> result =
                emiScheduleRepository.findUpcomingEmis(
                        LocalDate.now(), LocalDate.now().plusDays(5));

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindNextPendingEmi() {
        emiScheduleRepository.save(createEmi(8L, 80L, 2, LocalDate.now(), EmiStatus.PENDING));
        emiScheduleRepository.save(createEmi(8L, 80L, 1, LocalDate.now(), EmiStatus.OVERDUE));

        Optional<EmiSchedule> result =
                emiScheduleRepository.findNextPendingEmi(8L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getEmiNumber());
    }

    @Test
    void shouldCheckIfAllEmisPaid() {
        emiScheduleRepository.save(createEmi(9L, 90L, 1, LocalDate.now(), EmiStatus.PAID));

        boolean allPaid =
                emiScheduleRepository.areAllEmisPaid(9L);

        assertTrue(allPaid);
    }

    @Test
    void shouldGetTotalOutstandingAmount() {
        emiScheduleRepository.save(createEmi(10L, 100L, 1, LocalDate.now(), EmiStatus.PENDING));
        emiScheduleRepository.save(createEmi(10L, 100L, 2, LocalDate.now(), EmiStatus.PARTIAL_PAID));

        Optional<BigDecimal> total =
                emiScheduleRepository.getTotalOutstandingAmount(10L);

        assertTrue(total.isPresent());
        assertEquals(BigDecimal.valueOf(2000), total.get());
    }

    @Test
    void shouldDeleteByLoanId() {
        emiScheduleRepository.save(createEmi(11L, 110L, 1, LocalDate.now(), EmiStatus.PENDING));

        emiScheduleRepository.deleteByLoanId(11L);

        assertEquals(0, emiScheduleRepository.countByLoanId(11L));
    }

    @Test
    void shouldFindByCustomerIdAndStatus() {
        emiScheduleRepository.save(createEmi(12L, 120L, 1, LocalDate.now(), EmiStatus.OVERDUE));

        List<EmiSchedule> result =
                emiScheduleRepository.findByCustomerIdAndStatus(120L, EmiStatus.OVERDUE);

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindOverdueEmisByCustomer() {
        emiScheduleRepository.save(createEmi(
                13L, 130L, 1, LocalDate.now().minusDays(1), EmiStatus.PENDING));

        List<EmiSchedule> result =
                emiScheduleRepository.findOverdueEmisByCustomer(
                        130L, LocalDate.now());

        assertEquals(1, result.size());
    }
}
