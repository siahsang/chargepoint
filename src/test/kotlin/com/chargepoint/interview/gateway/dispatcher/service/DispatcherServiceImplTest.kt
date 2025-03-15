package com.chargepoint.interview.gateway.dispatcher.service

import com.chargepoint.interview.common.ChargeSessionEvent
import com.chargepoint.interview.gateway.dispatcher.dto.DispatcherStatus
import com.chargepoint.interview.gateway.dispatcher.exception.ChargeInfoInvalidException
import com.chargepoint.interview.gateway.dispatcher.service.producer.EventSender
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock

class DispatcherServiceImplTest {
    private val eventSenderMock = mock<EventSender<ChargeSessionEvent>>()
    private val dispatcherServiceImpl = DispatcherServiceImpl(eventSenderMock)


    @Test
    fun testDispatchRequest() {
        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"
        val callbackURL = "http://127.0.0.1:8080/test"
        //************************
        //          WHEN
        //************************
        val startTime = System.currentTimeMillis()
        val dispatcherStatus = dispatcherServiceImpl.dispatchRequest(
            stationId = stationId,
            driverToken = driverToken,
            callbackURL = callbackURL
        )
        val endTime = System.currentTimeMillis()
        //************************
        //          THEN
        //************************
        Assertions.assertThat(dispatcherStatus).isEqualTo(DispatcherStatus.ACCEPTED)
        Mockito.verify(eventSenderMock).send(
            argThat { event: ChargeSessionEvent ->
                event.stationId == stationId &&
                        event.driverToken == driverToken &&
                        event.callbackURL == callbackURL &&
                        event.startSessionTimestamp in startTime..endTime
            }
        )

    }

    @Test
    fun testDriverTokenLengthIsTooShort() {
        Assertions.assertThatExceptionOfType(ChargeInfoInvalidException::class.java).isThrownBy {
            dispatcherServiceImpl.dispatchRequest(
                stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38",
                driverToken = "short_token",
                callbackURL = "http://127.0.0.1:8080/test"
            )
        }
    }

    @Test
    fun testDriverTokenLengthIsTooLong() {
        Assertions.assertThatExceptionOfType(ChargeInfoInvalidException::class.java).isThrownBy {
            dispatcherServiceImpl.dispatchRequest(
                stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38",
                driverToken = "a".repeat(81),
                callbackURL = "http://127.0.0.1:8080/test"
            )
        }
    }

    @Test
    fun testDriverTokenContainsInvalidCharacters() {
        Assertions.assertThatExceptionOfType(ChargeInfoInvalidException::class.java).isThrownBy {
            dispatcherServiceImpl.dispatchRequest(
                stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38",
                driverToken = "invalid_token*",
                callbackURL = "http://127.0.0.1:8080/test"
            )
        }
    }

    @Test
    fun testDriverTokenIsValid() {
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "valid_driver_token_12345"
        val callbackURL = "http://127.0.0.1:8080/test"
        val dispatcherStatus = dispatcherServiceImpl.dispatchRequest(
            stationId = stationId,
            driverToken = driverToken,
            callbackURL = callbackURL
        )
        Assertions.assertThat(dispatcherStatus).isEqualTo(DispatcherStatus.ACCEPTED)
        Mockito.verify(eventSenderMock).send(
            argThat { event: ChargeSessionEvent ->
                event.stationId == stationId &&
                        event.driverToken == driverToken &&
                        event.callbackURL == callbackURL
            }
        )
    }

    @Test
    fun testStationIdIsInvalidUUID() {
        Assertions.assertThatExceptionOfType(ChargeInfoInvalidException::class.java).isThrownBy {
            dispatcherServiceImpl.dispatchRequest(
                stationId = "invalid-uuid",
                driverToken = "valid_driver_token_12345",
                callbackURL = "http://127.0.0.1:8080/test"
            )
        }
    }

    @Test
    fun callbackURLIsValid() {
        dispatcherServiceImpl.dispatchRequest(
            stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38",
            driverToken = "valid_driver_token_12345",
            callbackURL = "http://127.0.0.1:8080/test"
        )
    }

    @Test
    fun callbackURLIsInvalid() {
        Assertions.assertThatExceptionOfType(ChargeInfoInvalidException::class.java).isThrownBy {
            dispatcherServiceImpl.dispatchRequest(
                stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38",
                driverToken = "valid_driver_token_12345",
                callbackURL = "invalid-url"
            )
        }
    }

}