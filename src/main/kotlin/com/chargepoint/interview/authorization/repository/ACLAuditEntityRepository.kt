package com.chargepoint.interview.authorization.repository

import com.chargepoint.interview.authorization.domain.ACLAuditEntity
import org.springframework.data.repository.CrudRepository

interface ACLAuditEntityRepository : CrudRepository<ACLAuditEntity, Long> {
}