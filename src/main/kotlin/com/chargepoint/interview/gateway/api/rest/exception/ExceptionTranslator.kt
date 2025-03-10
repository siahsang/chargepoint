package com.chargepoint.interview.gateway.api.rest.exception

import com.chargepoint.interview.gateway.api.rest.dto.ErrorWrapper
import com.chargepoint.interview.gateway.dispatcher.exception.ChargeInfoInvalidException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

/**
 * Controller advice to handle exceptions globally and translate them into appropriate HTTP responses.
 */
@ControllerAdvice
@ResponseBody
class ExceptionTranslator : ResponseEntityExceptionHandler() {
    private val log = KotlinLogging.logger {}

    /**
     * Handles ChargeInfoInvalidException and translates it into an ErrorWrapper with a BAD_REQUEST status.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @param chargeInfoInvalidException The exception to handle.
     * @return An ErrorWrapper containing the error details.
     */
    @ExceptionHandler(ChargeInfoInvalidException::class)
    @ResponseBody
    fun handleEmailInvalidException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chargeInfoInvalidException: ChargeInfoInvalidException
    ): ErrorWrapper {
        return makeErrorWrapper(response, chargeInfoInvalidException, HttpStatus.BAD_REQUEST)
    }

    /**
     * Creates an ErrorWrapper from the given exception and HTTP status.
     *
     * @param response The HttpServletResponse object.
     * @param exception The exception to handle.
     * @param httpStatus The HTTP status to set in the response.
     * @return An ErrorWrapper containing the error details.
     */
    private fun makeErrorWrapper(
        response: HttpServletResponse,
        exception: Exception,
        httpStatus: HttpStatus
    ): ErrorWrapper {
        response.setStatus(httpStatus.value())
        val errMsg = "An error occurred invoking a REST service."
        if (Objects.nonNull(exception.cause)) {
            log.error(exception.cause) { errMsg }
        } else {
            log.error(exception) { errMsg }
        }

        return ErrorWrapper(httpStatus.value(), exception.message)
    }

    /**
     * Handles MethodArgumentNotValidException and translates it into an ErrorWrapper with the validation errors.
     *
     * @param ex The MethodArgumentNotValidException to handle.
     * @param headers The HttpHeaders object.
     * @param status The HTTP status code.
     * @param request The WebRequest object.
     * @return A ResponseEntity containing the ErrorWrapper with validation errors.
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.allErrors.map { it.defaultMessage }.joinToString(", ")
        return ResponseEntity(ErrorWrapper(status.value(), errors), headers, status)
    }
}