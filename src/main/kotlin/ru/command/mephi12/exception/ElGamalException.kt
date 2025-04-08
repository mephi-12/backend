package ru.command.mephi12.exception

import org.springframework.http.HttpStatus

/**
 * Базовое исключение для операций криптосистемы Эль-Гамаля
 */
open class ElGamalException(message: String) : AppException(
    status = HttpStatus.BAD_REQUEST,
    message = message
)

/**
 * Исключение, связанное с неверными входными параметрами для криптосистемы Эль-Гамаля
 */
class ElGamalParameterException(message: String) : ElGamalException(message)

/**
 * Исключение, связанное с операцией шифрования в криптосистеме Эль-Гамаля
 */
class ElGamalEncryptionException(message: String) : ElGamalException(message)

/**
 * Исключение, связанное с операцией расшифрования в криптосистеме Эль-Гамаля
 */
class ElGamalDecryptionException(message: String) : ElGamalException(message)

/**
 * Исключение, связанное с проверкой решения
 */
class ElGamalVerificationException(message: String) : ElGamalException(message)
