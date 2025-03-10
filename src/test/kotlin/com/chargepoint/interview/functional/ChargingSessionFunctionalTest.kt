package com.chargepoint.interview.functional

import com.chargepoint.helper.AbstractIntegrationTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.concurrent.TimeUnit

class ChargingSessionFunctionalTest : AbstractIntegrationTest() {

    val chargingURL = "/charge-point/v1/charge-session"


    @Test
    fun `WHEN start charging THEN happy path`() {


        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"

        `insert new acl data`(
            stationId = stationId,
            driverToken = driverToken,
            isAllowed = true
        )

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        wireMock.stubFor(
            put(urlEqualTo("/test"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )


        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        //************************
        //          WHEN
        //************************
        performPostRequest(chargingURL, requestContent, 200)

        //************************
        //          THEN
        //************************
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMock.verify(
                putRequestedFor(urlEqualTo("/test")).withRequestBody(
                    equalToJson(
                        """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "status":"allowed"
                             }"""
                    )
                )
            )
        }
    }


    @Test
    fun `WHEN send charge information THEN SHOULD get accepted response`() {


        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        //************************
        //          WHEN
        //************************
        val response = performPostRequest(chargingURL, requestContent, 200)

        //************************
        //          THEN
        //************************
        JSONAssert.assertEquals(
            """
            {
                "status": "accepted",
                "message": "Request is being processed asynchronously. The result will be sent to the provided callback URL."
            }
        """.trimIndent(), response, true
        )

    }

    @Test
    fun `WHEN send invalid driver's token THEN SHOULD get error response`() {

        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "bad_token_driver"

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        //************************
        //          WHEN
        //************************
        val response = performPostRequest(chargingURL, requestContent, 400)

        //************************
        //          THEN
        //************************
        JSONAssert.assertEquals(
            """
            {
              "http_status": 400,
              "message": "Driver token length is invalid. valid range is between 20 and 80"
            }
        """.trimIndent(), response, true
        )
    }


    @Test
    fun `WHEN authorization failed THEN SHOULD get not_allowed in callback`() {

        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        `insert new acl data`(
            stationId = stationId,
            driverToken = driverToken,
            isAllowed = false
        )

        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        wireMock.stubFor(
            put(urlEqualTo("/test"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        //************************
        //          WHEN
        //************************
        performPostRequest(chargingURL, requestContent, 200)


        //************************
        //          THEN
        //************************
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMock.verify(
                putRequestedFor(urlEqualTo("/test")).withRequestBody(
                    equalToJson(
                        """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "status":"not_allowed"
                             }"""
                    )
                )
            )
        }
    }


    @Test
    fun `WHEN driver's token not exist THEN SHOULD get invalid in callback`() {

        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123_not_exist"

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        wireMock.stubFor(
            put(urlEqualTo("/test"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        //************************
        //          WHEN
        //************************
        performPostRequest(chargingURL, requestContent, 200)

        //************************
        //          THEN
        //************************
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMock.verify(
                putRequestedFor(urlEqualTo("/test")).withRequestBody(
                    equalToJson(
                        """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "status":"invalid"
                             }"""
                    )
                )
            )
        }
    }

    @Test
    fun `WHEN authorizing THEN SHOULD audit acl decision`() {

        //************************
        //          Given
        //************************
        val stationId = "d546676f-0786-4fb7-a32c-17618a2b9e38"
        val driverToken = "token_driver_test_123"

        val callbackUrl = "http://127.0.0.1:${wireMock.port()}/test"

        `insert new acl data`(
            stationId = stationId,
            driverToken = driverToken,
            isAllowed = false
        )

        val requestContent = """{
                                 "station_id":"$stationId",
                                 "driver_token":"$driverToken",
                                 "call_back_url":"$callbackUrl"
                                }"""

        wireMock.stubFor(
            put(urlEqualTo("/test"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                )
        )

        //************************
        //          WHEN
        //************************
        performPostRequest(chargingURL, requestContent, 200)


        //************************
        //          THEN
        //************************
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val aclRecords = `get acl audit records`()
            Assertions.assertThat(aclRecords).hasSize(1)
            Assertions.assertThat(aclRecords[0]["driver_token"]).isEqualTo(driverToken)
            Assertions.assertThat(aclRecords[0]["station_id"]).isEqualTo(stationId)
            Assertions.assertThat(aclRecords[0]["authorization_status"]).isEqualTo("NOT_ALLOWED")
        }
    }



}
