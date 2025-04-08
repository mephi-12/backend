package ru.command.mephi12.utils.mathLibrary

import java.math.BigInteger
import java.security.SecureRandom

/**
 * Utility class for cryptographic random number generation
 */
object CryptoRandom {
    private val random = SecureRandom()

    /**
     * Generates a random BigInteger between min (inclusive) and max (exclusive)
     */
    fun randomBigInteger(min: BigInteger, max: BigInteger): BigInteger {
        if (min >= max) {
            throw IllegalArgumentException("min must be less than max")
        }

        val range = max.subtract(min)
        val length = range.bitLength()

        while (true) {
            val candidate = BigInteger(length, random)
            if (candidate.compareTo(range) < 0) {
                return min.add(candidate)
            }
        }
    }

    /**
     * Generates a random BigInteger with the specified bit length
     */
    fun randomBigInteger(bitLength: Int): BigInteger {
        return BigInteger(bitLength, random)
    }

    /**
     * Generates a random BigInteger that is coprime to n
     */
    fun randomCoprime(n: BigInteger): BigInteger {
        while (true) {
            val candidate = randomBigInteger(BigInteger.TWO, n)
            if (ModularArithmetic.gcd(candidate, n) == BigInteger.ONE) {
                return candidate
            }
        }
    }
}