package com.chargepoint.interview.authorization.service.consumer

import com.chargepoint.interview.authorization.service.AuthorizationService
import com.chargepoint.interview.common.ChargeSessionEvent
import com.chargepoint.interview.common.parseJson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * Implementation of the EventReceiver interface for handling ChargeSessionEvent messages from RabbitMQ.
 *
 * @property authorizationService The service used to authorize the charge session.
 */
@Component
class RabbitChargeEventReceiverImpl(val authorizationService: AuthorizationService) :
    EventReceiver<ChargeSessionEvent> {
    private val log = KotlinLogging.logger {}

    /**
     * Consumes messages from the specified RabbitMQ queue, parses the event, and handles it.
     *
     * @param event The JSON string representation of the ChargeSessionEvent.
     */
    @RabbitListener(
        queues = ["\${chargepoint.charge-session-queue.name}"],
        concurrency = "\${chargepoint.authorization.consumer.concurrency}"
    )
    fun consumeMessage(event: String) {
        kotlin.runCatching {
            log.info { "Received event: $event" }
            val chargeEvent = event.parseJson<ChargeSessionEvent>()
            onEvent(chargeEvent)
        }.onFailure {
            log.error(it) { "Failed to process event: $event" }
            // add to dead-letter queue
        }
    }

    /**
     * Handles the ChargeSessionEvent by invoking the authorization service.
     *
     * @param event The ChargeSessionEvent to be handled.
     */
    override fun onEvent(event: ChargeSessionEvent) {
        authorizationService.authorize(
            driverToken = event.driverToken,
            stationId = event.stationId,
            callBackUrl = event.callbackURL
        )
    }

}