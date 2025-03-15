package com.chargepoint.interview.authorization.service

import com.chargepoint.interview.authorization.domain.AuthorizationStatus
import com.chargepoint.interview.authorization.dto.CallbackPayload
import com.chargepoint.interview.authorization.repository.ACLEntityRepository
import com.chargepoint.interview.common.toJson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.Instant

/**
 * Service implementation for authorizing charge sessions.
 *
 * @property authorizationTimeOut The timeout duration for authorization in milliseconds.
 * @property aclAuditService The service used to audit ACL authorizations.
 * @property aclEntityRepository The repository used to access ACL entities.
 * @property restTemplate The RestTemplate used to make HTTP requests.
 */
@Service
class AuthorizationServiceImpl(
    @Value("\${chargepoint.authorization.acl.timeout-ms}")
    private val authorizationTimeOut: Long,
    private val aclAuditService: ACLAuditService,
    private val aclEntityRepository: ACLEntityRepository,
    private val restTemplate: RestTemplate
) : AuthorizationService {
    private val log = KotlinLogging.logger {}

    /**
     * Authorizes a charge session. If the driver's token is not found in the ACL, the authorization status is set to
     * INVALID. If the driver's token is found in the ACL and is allowed, the authorization status is set to ALLOWED
     * otherwise it is set to NOT_ALLOWED.
     *
     * @param driverToken The token of the driver requesting authorization.
     * @param stationId The identifier of the station where the charge session is taking place.
     * @param callBackUrl The URL to call back with the authorization result.
     * @param startSessionTimestamp The timestamp of the charge session event.
     */
    @Transactional(readOnly = true)
    override fun authorize(driverToken: String, stationId: String, callBackUrl: String, startSessionTimestamp: Long) {
        log.info { "Authorizing request: station-id $stationId, driver-token: $driverToken, callback: $callBackUrl" }

        val checkAuthorizationStatus = if (isSessionExpired(startSessionTimestamp)) {
            AuthorizationStatus.UNKNOWN
        } else {
            getAuthorizationStatus(driverToken, stationId)
        }

        aclAuditService.auditACL(
            driverToken = driverToken,
            stationId = stationId,
            authorizationStatus = checkAuthorizationStatus
        )

        notifyClient(
            callBackUrl = callBackUrl, callbackPayload = CallbackPayload(
                stationId = stationId,
                driverToken = driverToken,
                status = checkAuthorizationStatus.friendlyName
            )
        )
    }

    /**
     * Retrieves the authorization status for a given driver token and station ID.
     *
     * @param driverToken The token of the driver.
     * @param stationId The identifier of the station.
     * @return The authorization status.
     */
    private fun getAuthorizationStatus(driverToken: String, stationId: String): AuthorizationStatus {
        val aclEntity = aclEntityRepository.findByStationIdAndDriverToken(
            stationId = stationId,
            driverToken = driverToken
        )
        return if (aclEntity.isPresent) {
            if (aclEntity.get().isAllowed) AuthorizationStatus.ALLOWED else AuthorizationStatus.NOT_ALLOWED
        } else {
            AuthorizationStatus.INVALID
        }
    }

    /**
     * Notifies the client with the authorization result.
     *
     * @param callBackUrl The URL to call back with the authorization result.
     * @param callbackPayload The payload containing the authorization result.
     */
    private fun notifyClient(callBackUrl: String, callbackPayload: CallbackPayload) {
        log.info { "Notifying the callback ${callBackUrl} about the result of the decision" }
        restTemplate.put(callBackUrl, callbackPayload.toJson())
    }

    private fun isSessionExpired(startSessionTimestamp: Long): Boolean {
        val startTimestamp = Instant.ofEpochMilli(startSessionTimestamp)
        val currentTimeStamp = Instant.ofEpochMilli(System.currentTimeMillis())

        return Duration.between(startTimestamp, currentTimeStamp).toMillis() > authorizationTimeOut
    }
}