package com.chargepoint.interview.common

/**
 * Data class representing a charge session event.
 *
 * @property stationId The identifier of the station where the charge session is taking place.
 * @property driverToken The token of the driver involved in the charge session.
 * @property callbackURL The URL to call back with the result of the charge session.
 */
data class ChargeSessionEvent(
    val stationId: String,
    val driverToken: String,
    val callbackURL: String
)
