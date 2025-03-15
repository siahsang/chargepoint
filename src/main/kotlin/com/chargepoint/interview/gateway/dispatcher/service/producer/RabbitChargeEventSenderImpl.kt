package com.chargepoint.interview.gateway.dispatcher.service.producer

import com.chargepoint.interview.common.toJson
import com.chargepoint.interview.common.ChargeSessionEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Sending charge session events to a RabbitMQ queue.
 *
 * @property queueName The name of the RabbitMQ queue to send messages to.
 * @property rabbitTemplate The RabbitTemplate used for sending messages.
 */
@Service
class RabbitChargeEventSenderImpl(
    @Value("\${chargepoint.charge-session-queue.name}")
    private val queueName: String,
    private val rabbitTemplate: RabbitTemplate
) : EventSender<ChargeSessionEvent> {

    /**
     * Sends the given charge session event payload to the configured RabbitMQ queue.
     *
     * @param payload The charge session event to send.
     */
    override fun send(payload: ChargeSessionEvent) {
        rabbitTemplate.convertAndSend(queueName, payload.toJson())
    }
}