package com.chargepoint.interview.authorization.service.consumer


/**
 * A generic interface for receiving events.
 *
 * @param T The type of the event.
 */
fun interface EventReceiver<T> {
    /**
     * Method to handle the received event.
     *
     * @param event The event to be handled.
     */
    fun onEvent(event: T)
}