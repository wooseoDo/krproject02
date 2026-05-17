package kr.krproject02.domain.user.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserBirthDateCalculatorTest {

    private final UserBirthDateCalculator calculator = new UserBirthDateCalculator();

    @Test
    void parsesCurrentCenturyBirthDate() {
        LocalDate result = calculator.parseBirthDate("000101");

        assertThat(result).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    void parsesPreviousCenturyBirthDate() {
        LocalDate result = calculator.parseBirthDate("991231");

        assertThat(result).isEqualTo(LocalDate.of(1999, 12, 31));
    }

    @Test
    void returnsNullWhenBirthDateFormatIsInvalid() {
        LocalDate result = calculator.parseBirthDate("abc123");

        assertThat(result).isNull();
    }

    @Test
    void returnsNullWhenBirthDateDoesNotExist() {
        LocalDate result = calculator.parseBirthDate("991332");

        assertThat(result).isNull();
    }
}
