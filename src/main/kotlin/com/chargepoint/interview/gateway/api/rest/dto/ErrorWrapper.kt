package com.chargepoint.interview.gateway.api.rest.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data class representing an error response wrapper.
 *
 * @property httpStatus The HTTP status code of the error.
 * @property message The error message.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ErrorWrapper(
    @JsonProperty("http_status")
    val httpStatus: Int = 0,

    @JsonProperty("message")
    val message: String? = null
)

