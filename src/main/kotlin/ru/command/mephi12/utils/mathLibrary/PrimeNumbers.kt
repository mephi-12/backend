package ru.command.mephi12.utils.mathLibrary

import java.math.BigInteger
import java.security.SecureRandom

/**
 * Utility class for prime number operations
 */
object PrimeNumbers {
    private val random = SecureRandom()

    /**
     * Generates a random prime number with the specified bit length
     */
    fun generatePrime(bitLength: Int): BigInteger {
        return BigInteger.probablePrime(bitLength, random)
    }

    /**
     * Checks if a number is probably prime
     */
    fun isProbablePrime(n: BigInteger, certainty: Int = 100): Boolean {
        return n.isProbablePrime(certainty)
    }

    /**
     * Generates a safe prime (p where (p-1)/2 is also prime)
     */
    fun generateSafePrime(bitLength: Int): BigInteger {
        while (true) {
            val p = generatePrime(bitLength)
            val q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO)

            if (q.isProbablePrime(100)) {
                return p
            }
        }
    }
}