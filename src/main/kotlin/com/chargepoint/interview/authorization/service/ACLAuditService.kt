package com.chargepoint.interview.authorization.service

import com.chargepoint.interview.authorization.domain.AuthorizationStatus

/**
 * Auditing the decisions made in Access Control List (ACL).
 */
fun interface ACLAuditService {
    /**
     * Audits an ACL authorization attempt.
     *
     * @param driverToken The token of the driver involved in the authorization.
     * @param stationId The identifier of the station involved in the authorization.
     * @param authorizationStatus The status of the authorization.
     */
    fun auditACL(driverToken: String, stationId: String, authorizationStatus: AuthorizationStatus)
}