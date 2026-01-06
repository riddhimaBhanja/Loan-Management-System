package com.loanmanagement.emi.domain.service;

import com.loanmanagement.emi.domain.model.EmiSchedule;
import com.loanmanagement.emi.domain.model.EmiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculationServiceImplTest {

    private EmiCalculationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmiCalculationServiceImpl();
    }

    @Test
    void calculateEmi_success() {
        BigDecimal principal = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int tenure = 12;

        BigDecimal emi = service.calculateEmi(principal, rate, tenure);

        assertNotNull(emi);
        assertEquals(2, emi.scale());
        assertTrue(emi.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateEmi_invalidPrincipal() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateEmi(BigDecimal.ZERO, new BigDecimal("10"), 12)
        );
        assertEquals("Principal must be greater than zero", ex.getMessage());
    }

    @Test
    void calculateEmi_invalidRate() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateEmi(new BigDecimal("10000"), BigDecimal.ZERO, 12)
        );
        assertEquals("Interest rate must be greater than zero", ex.getMessage());
    }

    @Test
    void calculateEmi_invalidTenure() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateEmi(new BigDecimal("10000"), new BigDecimal("10"), 0)
        );
        assertEquals("Tenure must be greater than zero", ex.getMessage());
    }

    @Test
    void calculateTotalInterest_success() {
        BigDecimal emi = new BigDecimal("8884.17");
        BigDecimal principal = new BigDecimal("100000");
        int tenure = 12;

        BigDecimal totalInterest = service.calculateTotalInterest(emi, principal, tenure);

        assertNotNull(totalInterest);
        assertEquals(2, totalInterest.scale());
        assertTrue(totalInterest.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateTotalPayable_success() {
        BigDecimal emi = new BigDecimal("8884.17");
        int tenure = 12;

        BigDecimal totalPayable = service.calculateTotalPayable(emi, tenure);

        assertNotNull(totalPayable);
        assertEquals(2, totalPayable.scale());
        assertTrue(totalPayable.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void generateEmiSchedule_success() {
        Long loanId = 1L;
        Long customerId = 10L;
        BigDecimal principal = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int tenure = 12;
        LocalDate startDate = LocalDate.of(2026, 1, 1);

        List<EmiSchedule> scheduleList = service.generateEmiSchedule(
                loanId, customerId, principal, rate, tenure, startDate
        );

        assertNotNull(scheduleList);
        assertEquals(tenure, scheduleList.size());

        EmiSchedule first = scheduleList.get(0);
        EmiSchedule last = scheduleList.get(tenure - 1);

        assertEquals(loanId, first.getLoanId());
        assertEquals(customerId, first.getCustomerId());
        assertEquals(1, first.getEmiNumber());
        assertEquals(EmiStatus.PENDING, first.getStatus());

        assertEquals(BigDecimal.ZERO.setScale(2), last.getOutstandingBalance());
        assertEquals(tenure, last.getEmiNumber());
    }

    @Test
    void generateEmiSchedule_lastInstallmentClearsBalance() {
        List<EmiSchedule> scheduleList = service.generateEmiSchedule(
                2L,
                20L,
                new BigDecimal("50000"),
                new BigDecimal("10"),
                6,
                LocalDate.now()
        );

        EmiSchedule last = scheduleList.get(scheduleList.size() - 1);

        assertEquals(BigDecimal.ZERO.setScale(2), last.getOutstandingBalance());
    }
}
