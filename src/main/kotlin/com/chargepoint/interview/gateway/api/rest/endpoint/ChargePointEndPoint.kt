package com.chargepoint.interview.gateway.api.rest.endpoint

import com.chargepoint.interview.gateway.api.rest.dto.ChargeResponse
import com.chargepoint.interview.gateway.api.rest.dto.ChargingSessionRequest
import com.chargepoint.interview.gateway.api.rest.dto.ErrorWrapper
import com.chargepoint.interview.gateway.dispatcher.dto.DispatcherStatus
import com.chargepoint.interview.gateway.dispatcher.service.DispatcherService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/charge-point/v1")
@RestController
class ChargePointEndPoint(
    private val dispatcherService: DispatcherService
) {
    private val log = KotlinLogging.logger {}


    @Operation(
        summary = "If the charging session is successfully validated, this method will return a 201 status code and a success message in the response body.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "OK",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ChargeResponse::class),
                    examples = [
                        io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                            {
                                "status": "accepted",
                                "message": "Request is being processed asynchronously. The result will be sent to the provided callback URL."
                            }
                        """
                        )
                    ]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request, invalid format of the request. See response message for more information",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorWrapper::class),
                    examples = [
                        io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                            {
                                "http_status": 400,
                                "message": "Driver token length is invalid. valid range is between 20 and 80"
                            }
                        """
                        )
                    ]
                )]
            )
        ]
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/charge-session")
    fun startChargeSession(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Charging session information",
            required = true,
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ChargingSessionRequest::class),
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        value = """
                            {
                                "station_id": "d546676f-0786-4fb7-a32c-17618a2b9e38",
                                "driver_token": "driver_token_test_123456",
                                "call_back_url": "http://127.0.0.1:9999"
                            }
                        """
                    )
                ]
            )],
        )
        @RequestBody @Valid chargingSessionRequest: ChargingSessionRequest
    ): ResponseEntity<ChargeResponse> {
        val dispatcherStatus = dispatcherService.dispatchRequest(
            stationId = chargingSessionRequest.stationId!!,
            driverToken = chargingSessionRequest.driverToken!!,
            callbackURL = chargingSessionRequest.callbackURL!!
        )

        return if (dispatcherStatus == DispatcherStatus.ACCEPTED) {
            ResponseEntity.ok(
                ChargeResponse(
                    "accepted",
                    "Request is being processed asynchronously. The result will be sent to the provided callback URL."
                )
            )
        } else {
            ResponseEntity(
                ChargeResponse(
                    "failed",
                    "Request failed. Please check the request and try again."
                ), HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }
}