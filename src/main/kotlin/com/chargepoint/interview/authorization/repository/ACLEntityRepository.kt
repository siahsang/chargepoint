package com.chargepoint.interview.authorization.repository

import com.chargepoint.interview.authorization.domain.ACLEntity
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface ACLEntityRepository : CrudRepository<ACLEntity, Long> {
    fun findByStationIdAndDriverToken(stationId: String, driverToken: String): Optional<ACLEntity>
}