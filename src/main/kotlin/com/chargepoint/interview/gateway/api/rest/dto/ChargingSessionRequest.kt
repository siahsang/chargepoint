package com.chargepoint.interview.gateway.api.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

/**
 * Data class representing a request to start a charging session.
 *
 * @property driverToken The token of the driver requesting the charging session.
 * @property stationId The identifier of the station where the charging session is requested.
 * @property callbackURL The URL to call back with the result of the charging session.
 */
data class ChargingSessionRequest(
    @JsonProperty("driver_token")
    @field:NotNull(message = "Driver token is required")
    val driverToken: String?,

    @JsonProperty("station_id")
    @field:NotNull(message = "Station id is required")
    val stationId: String?,

    @JsonProperty("call_back_url")
    @field:NotNull(message = "Call back URL is required")
    val callbackURL: String?
)
