package ru.command.mephi12.utils.mathLibrary

import java.math.BigInteger

/**
 * Utility class for finite group theory operations
 */
object GroupTheory {
    /**
     * Функция-расширение для создания диапазона BigInteger
     */
    private infix fun BigInteger.until(to: BigInteger): Iterable<BigInteger> {
        val start = this
        val endExclusive = to
        return object : Iterable<BigInteger> {
            override fun iterator(): Iterator<BigInteger> {
                return object : Iterator<BigInteger> {
                    private var current = start

                    override fun hasNext(): Boolean = current < endExclusive

                    override fun next(): BigInteger {
                        if (!hasNext()) throw NoSuchElementException()
                        val result = current
                        current = current.add(BigInteger.ONE)
                        return result
                    }
                }
            }
        }
    }

    /**
     * Finds a generator of the multiplicative group Z_p*
     * Uses probabilistic approach that works well for educational examples
     */
    fun findGenerator(p: BigInteger): BigInteger {
        // Simple generator finding for small p
        if (p.bitLength() < 32) {
            return findGeneratorByTesting(p)
        }

        // For larger p, use probabilistic approach
        val pMinus1 = p.subtract(BigInteger.ONE)

        // Try to factorize p-1 to find its prime factors
        // For educational examples, often p = 2q+1 where q is prime
        val q = pMinus1.divide(BigInteger.TWO)

        for (attempt in 1..50) {
            val g = CryptoRandom.randomBigInteger(BigInteger.TWO, p.subtract(BigInteger.ONE))

            // Check if g^(p-1) ≡ 1 (mod p) as required by Fermat's Little Theorem
            if (ModularArithmetic.modPow(g, pMinus1, p) != BigInteger.ONE) {
                continue
            }

            // If p = 2q+1 is a safe prime, check if g is a generator
            if (PrimeNumbers.isProbablePrime(q)) {
                if (ModularArithmetic.modPow(g, BigInteger.TWO, p) != BigInteger.ONE &&
                    ModularArithmetic.modPow(g, q, p) != BigInteger.ONE) {
                    return g
                }
            } else {
                // For general case, simplified check
                if (isLikelyGenerator(g, p)) {
                    return g
                }
            }
        }

        // Default fallback, often 2 or 3 are generators
        return if (isLikelyGenerator(BigInteger.TWO, p))
            BigInteger.TWO else BigInteger.valueOf(3)
    }

    /**
     * More thorough generator testing for small primes
     */
    private fun findGeneratorByTesting(p: BigInteger): BigInteger {
        val pMinus1 = p.subtract(BigInteger.ONE)

        // Проверим последовательно числа от 2 до p-1
        var g = BigInteger.TWO
        while (g.compareTo(p) < 0) {
            // Using a set to track all generated elements
            val elements = mutableSetOf<BigInteger>()
            var current = BigInteger.ONE

            var i = BigInteger.ONE
            while (i.compareTo(p) < 0) {
                current = ModularArithmetic.modMultiply(current, g, p)
                elements.add(current)
                if (elements.size == pMinus1.intValueExact()) {
                    return g
                }
                i = i.add(BigInteger.ONE)
            }

            g = g.add(BigInteger.ONE)
        }

        // Fallback
        return BigInteger.TWO
    }

    /**
     * Quick test to check if a number is likely a generator
     */
    private fun isLikelyGenerator(g: BigInteger, p: BigInteger): Boolean {
        // Compute a few powers and make sure they're all different
        val values = mutableSetOf<BigInteger>()
        var current = BigInteger.ONE

        for (i in 1..10) {
            current = ModularArithmetic.modMultiply(current, g, p)
            if (values.contains(current)) return false
            values.add(current)
        }

        return true
    }
}