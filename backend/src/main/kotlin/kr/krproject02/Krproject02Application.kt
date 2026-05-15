package kr.krproject02

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class Krproject02Application

fun main(args: Array<String>) {
    runApplication<Krproject02Application>(*args)
}
