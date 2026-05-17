package kr.krproject02;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

class Krproject02ApplicationTests {

    @Test
    void applicationClassHasSpringBootApplicationAnnotation() {
        assertThat(Krproject02Application.class.getAnnotation(SpringBootApplication.class))
            .isNotNull();
    }
}
