package com.chargepoint.interview.authorization.domain

import jakarta.persistence.*


/**
 * Entity class representing an Access Control List (ACL) entry.
 *
 */
@Entity
@Table(name = "ACL_ENTITY", indexes = [Index(name = "station_driver_idx", columnList = "station_id,driver_token")])
class ACLEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, name = "station_id")
    val stationId: String,

    @Column(nullable = false, name = "driver_token")
    var driverToken: String,

    @Column(nullable = false, name = "is_allowed")
    val isAllowed: Boolean,
)


