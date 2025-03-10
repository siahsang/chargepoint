package com.chargepoint.interview.gateway.api.rest.dto

/**
 * Data class representing a response to a charge request.
 *
 * @property status The status of the charge request.
 * @property message A message providing additional information about the charge request.
 */
data class ChargeResponse (
    val status: String,
    val message: String
)
