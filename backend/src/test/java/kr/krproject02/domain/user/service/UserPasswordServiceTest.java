package kr.krproject02.domain.user.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPasswordServiceTest {

    private final UserPasswordService passwordService = new UserPasswordService();

    @Test
    void encodesPasswordWithHashInsteadOfPlainText() {
        String encoded = passwordService.encode("4068");

        assertThat(encoded).isNotEqualTo("4068");
        assertThat(encoded).startsWith("$2");
    }

    @Test
    void matchesOriginalPasswordWithEncodedHash() {
        String encoded = passwordService.encode("4068");

        assertThat(passwordService.matches("4068", encoded)).isTrue();
        assertThat(passwordService.matches("wrong", encoded)).isFalse();
    }
}
