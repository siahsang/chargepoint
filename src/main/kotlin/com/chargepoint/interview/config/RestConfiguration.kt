package com.chargepoint.interview.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration


@Configuration
class RestConfiguration {
    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder
            .connectTimeout(Duration.ofMillis(3000))
            .readTimeout(Duration.ofMillis(3000))
            .build()
    }
}