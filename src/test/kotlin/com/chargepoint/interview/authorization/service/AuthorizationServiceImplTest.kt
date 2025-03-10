package com.chargepoint.interview.authorization.service

import com.chargepoint.interview.authorization.domain.ACLEntity
import com.chargepoint.interview.authorization.domain.AuthorizationStatus
import com.chargepoint.interview.authorization.dto.CallbackPayload
import com.chargepoint.interview.authorization.repository.ACLEntityRepository
import com.chargepoint.interview.common.toJson
import org.junit.jupiter.api.Test
import org.mockito.AdditionalAnswers
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.springframework.web.client.RestTemplate
import java.util.*


class AuthorizationServiceImplTest {
    private val aclAuditServiceMock = mock<ACLAuditService>()
    private val aclEntityRepositoryMock = mock<ACLEntityRepository>()
    private val restTemplateMock = mock<RestTemplate>()

    private val authorizationServiceImpl = AuthorizationServiceImpl(
        authorizationTimeOut = 1000,
        aclAuditService = aclAuditServiceMock,
        aclEntityRepository = aclEntityRepositoryMock,
        restTemplate = restTemplateMock
    )

    @Test
    fun testAuthorize() {
        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"
        val callbackURL = "http://127.0.0.1:8080/test"

        //************************
        //          WHEN
        //************************
        Mockito.`when`(
            aclEntityRepositoryMock.findByStationIdAndDriverToken(
                stationId = stationId,
                driverToken = driverToken
            )
        ).thenReturn(Optional.of(ACLEntity(stationId = stationId, driverToken = driverToken, isAllowed = true)))

        authorizationServiceImpl.authorize(driverToken = driverToken, stationId = stationId, callBackUrl = callbackURL)

        //************************
        //          THEN
        //************************
        Mockito.verify(aclAuditServiceMock).auditACL(
            driverToken = driverToken,
            stationId = stationId,
            authorizationStatus = AuthorizationStatus.ALLOWED
        )

        Mockito.verify(restTemplateMock).put(
            callbackURL,
            CallbackPayload(
                stationId = stationId,
                driverToken = driverToken,
                status = AuthorizationStatus.ALLOWED.friendlyName
            ).toJson()
        )
    }

    @Test
    fun testAuthorizeWhenLongerThanExpectedThenTimeOut() {
        //************************
        //         Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"
        val callbackURL = "http://127.0.0.1:8080/test"

        Mockito.`when`(
            aclEntityRepositoryMock.findByStationIdAndDriverToken(
                stationId = stationId,
                driverToken = driverToken
            )
        ).thenAnswer(
            AdditionalAnswers.answersWithDelay(
                2000, {
                    Optional.of(ACLEntity(stationId = stationId, driverToken = driverToken, isAllowed = true))
                }
            )
        )

        //************************
        //         WHEN
        //************************
        authorizationServiceImpl.authorize(driverToken = driverToken, stationId = stationId, callBackUrl = callbackURL)

        //************************
        //         THEN
        //************************

        Mockito.verify(aclAuditServiceMock).auditACL(
            driverToken = driverToken,
            stationId = stationId,
            authorizationStatus = AuthorizationStatus.UNKNOWN
        )

        Mockito.verify(restTemplateMock).put(
            callbackURL,
            CallbackPayload(
                stationId = stationId,
                driverToken = driverToken,
                status = AuthorizationStatus.UNKNOWN.friendlyName
            ).toJson()
        )
    }


}

