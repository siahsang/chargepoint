package com.chargepoint.helper

import com.github.tomakehurst.wiremock.WireMockServer
import jakarta.annotation.Resource
import org.junit.jupiter.api.BeforeEach
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.RabbitMQContainer
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock


@SpringBootTest
@ContextConfiguration(classes = [IntegrationTestConfig::class])
@EnableWireMock
abstract class AbstractIntegrationTest : SpringMockMVCHelper() {

    @Resource
    protected lateinit var jdbcTemplate: JdbcTemplate

    @Value("\${chargepoint.charge-session-queue.name}")
    private lateinit var defaultTopic: String


    @Resource
    private lateinit var rabbitAdmin: RabbitAdmin

    @InjectWireMock
    lateinit var wireMock: WireMockServer

    companion object {
        @JvmStatic
        val rabbitMQContainer = RabbitMQContainer("rabbitmq:3.11-management")

        init {
            rabbitMQContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost)
            registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort)
            registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername)
            registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword)
        }
    }


    @BeforeEach
    fun beforeEachTest() {
        rabbitAdmin.purgeQueue(defaultTopic)
        wireMock.resetMappings()
        H2Database.truncateAllTables(jdbcTemplate)
    }

    protected fun `insert new acl data`(driverToken: String, stationId: String, isAllowed: Boolean) {
        val sql = """
            INSERT INTO acl_entity (driver_token, station_id, is_allowed)
            VALUES (?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(sql, driverToken, stationId, isAllowed)
    }

    protected fun `get acl audit records`(): List<Map<String, Any>> {
        val sql = """
            SELECT * FROM acl_audit_entity
        """.trimIndent()

       return jdbcTemplate.queryForList(sql)
    }
}
