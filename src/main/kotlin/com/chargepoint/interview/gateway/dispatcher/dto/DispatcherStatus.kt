package com.chargepoint.interview.gateway.dispatcher.dto

/**
 * Enum class representing the status of a dispatcher.
 *
 * @property friendlyName A user-friendly name for the status.
 */
enum class DispatcherStatus(friendlyName: String) {
    ACCEPTED("accepted"),
    FAILED("failed")
}