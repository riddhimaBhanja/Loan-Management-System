package com.loanmanagement.loanapp.domain.repository;

import com.loanmanagement.loanapp.domain.model.LoanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class LoanTypeRepositoryTest {

    @Autowired
    private LoanTypeRepository loanTypeRepository;

    private LoanType createLoanType(String name, boolean isActive) {
        return LoanType.builder()
                .name(name)
                .description("Test description")
                .minAmount(BigDecimal.valueOf(10000))
                .maxAmount(BigDecimal.valueOf(500000))
                .minTenureMonths(6)
                .maxTenureMonths(60)
                .interestRate(BigDecimal.valueOf(10.5))
                .isActive(isActive)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByName_shouldReturnLoanType() {
        loanTypeRepository.save(createLoanType("Home Loan", true));

        Optional<LoanType> loanType =
                loanTypeRepository.findByName("Home Loan");

        assertTrue(loanType.isPresent());
        assertEquals("Home Loan", loanType.get().getName());
    }

    @Test
    void findByIsActiveTrue_shouldReturnActiveLoanTypes() {
        loanTypeRepository.save(createLoanType("Car Loan", true));
        loanTypeRepository.save(createLoanType("Old Loan", false));

        List<LoanType> activeLoanTypes =
                loanTypeRepository.findByIsActiveTrue();

        assertEquals(1, activeLoanTypes.size());
        assertTrue(activeLoanTypes.get(0).getIsActive());
    }

    @Test
    void existsByName_shouldReturnTrueWhenExists() {
        loanTypeRepository.save(createLoanType("Personal Loan", true));

        boolean exists =
                loanTypeRepository.existsByName("Personal Loan");

        assertTrue(exists);
    }

    @Test
    void existsByName_shouldReturnFalseWhenNotExists() {
        boolean exists =
                loanTypeRepository.existsByName("Non Existing Loan");

        assertFalse(exists);
    }
}
