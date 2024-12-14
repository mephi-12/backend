package ru.command.mephi12.utils

import ru.command.mephi12.exception.AppException
import java.math.BigInteger
import java.security.SecureRandom

fun List<BigInteger>.sum() : BigInteger {
    var res = BigInteger.ZERO
    for(el in this) {
        res += el
    }
    return res
}

fun generateCoprime(m: BigInteger): BigInteger {
    require(m > BigInteger.ONE) { "m должно быть больше 1" }

    val rnd = SecureRandom()
    var candidate: BigInteger
    var attempts = 0
    do {
        attempts++
        if(attempts > 10000) {
            throw AppException("Не удалось найти взаимнопростое число за 10000 попыток")
        }
        // Генерируем случайное число с битовой длиной не меньше длины m
        candidate = BigInteger(m.bitLength(), rnd)
        // Если получилось число больше или равное m или меньше 2, корректируем его
        if (candidate < BigInteger.TWO) {
            candidate = candidate.mod(m).max(BigInteger.TWO)
        } else if (candidate >= m) {
            candidate = candidate.mod(m)
            if (candidate < BigInteger.TWO) {
                candidate = candidate.add(BigInteger.TWO)
            }
        }
    } while (candidate.gcd(m) != BigInteger.ONE)

    return candidate
}