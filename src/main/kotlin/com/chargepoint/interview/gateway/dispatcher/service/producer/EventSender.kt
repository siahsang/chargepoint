package com.chargepoint.interview.gateway.dispatcher.service.producer


/**
 * Functional interface for sending events with a payload of type T.
 *
 * @param T The type of the payload to be sent.
 */
fun interface EventSender<T> {
    /**
     * Sends the given payload.
     *
     * @param payload The payload to send.
     */
    fun send(payload: T)
}