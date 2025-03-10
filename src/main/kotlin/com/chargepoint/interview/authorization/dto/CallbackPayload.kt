package com.chargepoint.interview.authorization.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data class representing the payload for a callback.
 */
data class CallbackPayload(
    @JsonProperty("station_id")
    val stationId: String,

    @JsonProperty("driver_token")
    val driverToken: String,

    @JsonProperty("status")
    val status: String
)
