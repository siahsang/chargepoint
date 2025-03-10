package com.chargepoint.interview.authorization.domain

/**
 * Enum class representing the status of an authorization decision.
 */
enum class AuthorizationStatus(val friendlyName: String) {
    ALLOWED("allowed"),
    NOT_ALLOWED("not_allowed"),
    INVALID("invalid"),
    UNKNOWN("unknown")
}