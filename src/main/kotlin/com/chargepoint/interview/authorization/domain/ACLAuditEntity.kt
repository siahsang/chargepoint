package com.chargepoint.interview.authorization.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime


/**
 * Entity class representing a decision made for (ACL) authorization.
 */

@Entity
@Table(name = "ACL_AUDIT_ENTITY")
class ACLAuditEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, name = "station_id")
    val stationId: String,

    @Column(nullable = false, name = "driver_token")
    val driverToken: String,

    @Column(nullable = false, name = "created_date_time")
    @CreationTimestamp
    val createdDateTime: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "authorization_status")
    val authorizationStatus: AuthorizationStatus,
)


