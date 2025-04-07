package ru.command.mephi12.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

@RestControllerAdvice
class ExceptionResolver {
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: HttpServletRequest, response: HttpServletResponse) {
        val exceptionToResponse = toApiError(exception)
        val objectMapper = ObjectMapper().findAndRegisterModules()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.status = exceptionToResponse.status.value()
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(exceptionToResponse))
    }

    private fun toApiError(exception: Exception): AppException =
        when (exception) {
            is AppException -> exception
            is MethodArgumentNotValidException ->
                AppException(
                    status = HttpStatus.BAD_REQUEST,
                    message = exception.fieldErrors.joinToString(separator = "\n") { "${it.field}: ${it.defaultMessage}" }
                )
            else -> AppException(message = exception.message.orEmpty())
    }
}