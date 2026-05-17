package kr.krproject02.domain.user.validation;

import kr.krproject02.domain.user.error.UserErrorCode;
import kr.krproject02.domain.user.error.UserException;
import kr.krproject02.domain.user.service.UserBirthDateCalculator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class UserAccountValidatorTest {

    private final UserAccountValidator validator = new UserAccountValidator(new UserBirthDateCalculator());

    @Test
    void acceptsValidRegistrationInput() {
        assertThatCode(() -> validator.validateRegistration("950312", "4068"))
            .doesNotThrowAnyException();
    }

    @Test
    void rejectsInvalidBirthDate() {
        assertThatThrownBy(() -> validator.validateRegistration("991332", "4068"))
            .isInstanceOf(UserException.class)
            .extracting("errorCode")
            .isEqualTo(UserErrorCode.INVALID_BIRTH_DATE);
    }

    @Test
    void rejectsInvalidPassword() {
        assertThatThrownBy(() -> validator.validateLogin("950312", "123"))
            .isInstanceOf(UserException.class)
            .extracting("errorCode")
            .isEqualTo(UserErrorCode.INVALID_PASSWORD);
    }
}
