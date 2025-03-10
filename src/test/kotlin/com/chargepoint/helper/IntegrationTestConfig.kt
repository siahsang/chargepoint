package com.chargepoint.helper

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("integration-test")
@ComponentScan(value = ["com.chargepoint"])
@Configuration
class IntegrationTestConfig {
    @Value("\${chargepoint.charge-session-queue.name}")
    private lateinit var defaultQueueName: String


    @Bean
    fun defaultQueueName(): Queue {
        return Queue(defaultQueueName, false)
    }
}
