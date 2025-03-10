package com.chargepoint.interview.gateway.dispatcher.service

import com.chargepoint.interview.gateway.dispatcher.dto.DispatcherStatus

/**
 * Dispatching requests.
 */
fun interface DispatcherService {
    /**
     * Dispatches a request with the given parameters.
     *
     * @param stationId The ID of the station.
     * @param driverToken The token of the driver.
     * @param callbackURL The callback URL to be used.
     * @return The status of the dispatcher.
     */
    fun dispatchRequest(
        stationId: String,
        driverToken: String,
        callbackURL: String
    ): DispatcherStatus
}