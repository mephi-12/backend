package ru.command.mephi12.exception

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonIgnore


@JsonIgnoreProperties(
    "localizedMessage",
    "cause",
    "stackTrace",
    "ourStackTrace",
    "suppressed"
)
class AppException(
    var status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    override val message: String = status.name,
    val errorDescription: ErrorDescription = ErrorDescription("", ""),
    errors: List<FieldError> = emptyList(),
    cause: Throwable? = null,
) : Exception(cause) {

    constructor(msg: String) : this(message = msg)

    @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss[.SSS]")
    val timestamp = LocalDateTime.now()
}

class ErrorDescription(
    val ru: String? = null,
    val en: String? = null,
) {
    companion object {
        @JsonIgnore
        val BAD_REQUEST = ErrorDescription("Bad request", "Bad request")
    }
}

