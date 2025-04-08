package ru.command.mephi12.utils.mathLibrary

import java.math.BigInteger

/**
 * Utility class for modular arithmetic operations
 */
object ModularArithmetic {
    /**
     * Computes (base^exponent) mod modulus efficiently
     */
    fun modPow(base: BigInteger, exponent: BigInteger, modulus: BigInteger): BigInteger {
        return base.modPow(exponent, modulus)
    }

    /**
     * Computes (a * b) mod n
     */
    fun modMultiply(a: BigInteger, b: BigInteger, n: BigInteger): BigInteger {
        return a.multiply(b).mod(n)
    }

    /**
     * Computes modular multiplicative inverse: a^(-1) mod n
     * Returns null if the inverse doesn't exist
     */
    fun modInverse(a: BigInteger, n: BigInteger): BigInteger? {
        return try {
            a.modInverse(n)
        } catch (e: ArithmeticException) {
            null // No inverse exists
        }
    }

    /**
     * Computes greatest common divisor of a and b
     */
    fun gcd(a: BigInteger, b: BigInteger): BigInteger {
        return a.gcd(b)
    }

    /**
     * Extended Euclidean algorithm: computes coefficients s and t
     * such that a*s + b*t = gcd(a,b)
     * @return Triple(gcd, s, t)
     */
    fun extendedGcd(a: BigInteger, b: BigInteger): Triple<BigInteger, BigInteger, BigInteger> {
        if (b == BigInteger.ZERO) {
            return Triple(a, BigInteger.ONE, BigInteger.ZERO)
        }

        val (gcd, s1, t1) = extendedGcd(b, a.mod(b))
        val s = t1
        val t = s1.subtract(a.divide(b).multiply(t1))
        return Triple(gcd, s, t)
    }
}