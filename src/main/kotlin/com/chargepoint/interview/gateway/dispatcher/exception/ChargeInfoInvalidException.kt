package com.chargepoint.interview.gateway.dispatcher.exception

import java.lang.RuntimeException

/**
 * Enum class representing the status of a dispatcher.
 *
 * @property friendlyName A user-friendly name for the status.
 */
class ChargeInfoInvalidException(message: String) : RuntimeException(message) {

}