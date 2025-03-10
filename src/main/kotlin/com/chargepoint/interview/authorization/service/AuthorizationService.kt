package com.chargepoint.interview.authorization.service

/**
 * Authorizing charge sessions.
 */
interface AuthorizationService {
    /**
     * Authorizes a charge session.
     *
     * @param driverToken The token of the driver requesting authorization.
     * @param stationId The identifier of the station where the charge session is taking place.
     * @param callBackUrl The URL to call back with the authorization result.
     */
    fun authorize(driverToken: String, stationId: String, callBackUrl: String)
}