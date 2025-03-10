package com.chargepoint.interview.authorization.service

import com.chargepoint.interview.authorization.domain.ACLAuditEntity
import com.chargepoint.interview.authorization.domain.AuthorizationStatus
import com.chargepoint.interview.authorization.repository.ACLAuditEntityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ACLAuditServiceImpl(private val aclAuditEntityRepository: ACLAuditEntityRepository) : ACLAuditService {

    /**
     * Audits an ACL authorization attempt by saving the details to the repository.
     *
     * @param driverToken The token of the driver involved in the authorization.
     * @param stationId The identifier of the station involved in the authorization.
     * @param authorizationStatus The status of the authorization.
     */
    @Transactional
    override fun auditACL(driverToken: String, stationId: String, authorizationStatus: AuthorizationStatus) {
        aclAuditEntityRepository.save(
            ACLAuditEntity(
                driverToken = driverToken,
                stationId = stationId,
                authorizationStatus = authorizationStatus
            )
        )
    }
}