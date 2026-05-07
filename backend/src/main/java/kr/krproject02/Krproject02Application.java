package kr.krproject02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Krproject02Application {

    public static void main(String[] args) {
        SpringApplication.run(Krproject02Application.class, args);
    }

}
