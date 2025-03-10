package com.chargepoint.interview.gateway.dispatcher.service

import com.chargepoint.interview.common.ChargeSessionEvent
import com.chargepoint.interview.gateway.dispatcher.dto.DispatcherStatus
import com.chargepoint.interview.gateway.dispatcher.exception.ChargeInfoInvalidException
import com.chargepoint.interview.gateway.dispatcher.service.producer.EventSender
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service implementation for dispatching requests.
 *
 * @property eventSender The event sender used to send charge session events.
 */
@Service
class DispatcherServiceImpl(private val eventSender: EventSender<ChargeSessionEvent>) : DispatcherService {
    private val log = KotlinLogging.logger {}

    /**
     * First check if request is valid, then dispatch the request to a broker and return the status.
     *
     * @param stationId The ID of the station.
     * @param driverToken The token of the driver.
     * @param callbackURL The callback URL to be used.
     * @return The status of the dispatcher.
     */
    override fun dispatchRequest(
        stationId: String,
        driverToken: String,
        callbackURL: String
    ): DispatcherStatus {
        throwsExceptionIfStationIdInvalid(stationId)
        throwsExceptionIfDriverTokenInvalid(driverToken)
        throwsExceptionIfCallbackURLInvalid(callbackURL)

        log.info { "Dispatching request for driverToken: $driverToken, stationId: $stationId, callbackURL: $callbackURL" }

        return runCatching {
            // Attempt to send the charge event
            eventSender.send(
                ChargeSessionEvent(
                    driverToken = driverToken,
                    stationId = stationId,
                    callbackURL = callbackURL
                )
            )
            log.info { "Dispatched request for driverToken: $driverToken, stationId: $stationId with status: ${DispatcherStatus.ACCEPTED}" }
            DispatcherStatus.ACCEPTED
        }.onFailure { exception ->
            log.error(exception) { "Failed to dispatch request for driverToken: $driverToken, stationId: $stationId" }
        }.getOrElse {
            DispatcherStatus.FAILED
        }
    }

    /**
     * Validates the driver token and throws an exception if it is invalid.
     *
     * @param driverToken The token of the driver.
     * @throws ChargeInfoInvalidException if the driver token is invalid.
     */
    private fun throwsExceptionIfDriverTokenInvalid(driverToken: String) {
        if (driverToken.length < 20 || driverToken.length > 80) {
            throw ChargeInfoInvalidException("Driver token length is invalid. valid range is between 20 and 80")
        }

        if (!driverToken.matches(Regex("^[A-Za-z0-9\\-._~]+$"))) {
            throw ChargeInfoInvalidException("Driver token contains invalid characters")
        }
    }

    /**
     * Validates the station ID and throws an exception if it is invalid.
     *
     * @param stationId The ID of the station.
     * @throws ChargeInfoInvalidException if the station ID is invalid.
     */
    private fun throwsExceptionIfStationIdInvalid(stationId: String) {
        kotlin.runCatching { UUID.fromString(stationId) }.onFailure {
            throw ChargeInfoInvalidException("Station ID is invalid, it should be a valid UUID")
        }
    }

    /**
     * Validates the callback URL and throws an exception if it is invalid.
     *
     * @param callbackURL The callback URL to be used.
     * @throws ChargeInfoInvalidException if the callback URL is invalid.
     */
    private fun throwsExceptionIfCallbackURLInvalid(callbackURL: String) {
        if (!UrlValidator.getInstance().isValid(callbackURL)) {
            throw ChargeInfoInvalidException("Callback URL is invalid, it should be a valid URL")
        }
    }
}