package com.chargepoint.interview.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.RabbitMQContainer


@Profile("docker", "dev")
@Configuration
class DockerRabbitConfiguration {
    @Value("\${chargepoint.charge-session-queue.name}")
    private lateinit var defaultQueueName: String


    @Bean
    fun chargeSessionQueueName(): Queue {
        return Queue(defaultQueueName, false)
    }
}


@Profile("dev")
@Configuration
class DevRabbitConfiguration {
    private val log = KotlinLogging.logger {}

    @Bean(destroyMethod = "stop")
    fun rabbitMQContainer(): RabbitMQContainer {
        val rabbitMQContainer = RabbitMQContainer("rabbitmq:3.11-management")
        rabbitMQContainer.start()

        log.info { "Started RabbitMQ on port:${rabbitMQContainer.amqpPort}, ui-port:${rabbitMQContainer.httpPort} , user:${rabbitMQContainer.adminUsername}," +
                " password:${rabbitMQContainer.adminPassword},  " }
        return rabbitMQContainer
    }

    @Bean
    fun connectionFactory(rabbitMQContainer: RabbitMQContainer): CachingConnectionFactory {
        return CachingConnectionFactory().apply {
            setHost(rabbitMQContainer.host)
            setPassword(rabbitMQContainer.adminPassword)
            port = rabbitMQContainer.amqpPort
            username = rabbitMQContainer.adminUsername
            virtualHost = "/"
        }
    }

    @Bean
    fun rabbitTemplate(connectionFactory: CachingConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory)
    }
}
