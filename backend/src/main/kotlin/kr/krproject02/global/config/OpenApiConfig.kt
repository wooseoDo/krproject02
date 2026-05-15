package kr.krproject02.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun krproject02OpenApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("KRProject02 API 문서")
                    .description("일반 사용자와 관리자 scope를 분리한 API 문서")
                    .version("v1"),
            )
}
