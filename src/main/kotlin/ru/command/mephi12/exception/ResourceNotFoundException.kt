package ru.command.mephi12.exception

import org.springframework.http.HttpStatus

class ResourceNotFoundException(resource: Any) : AppException(status = HttpStatus.NOT_FOUND, message = "Resource $resource not found")