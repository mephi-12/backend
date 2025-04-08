package ru.command.mephi12.utils.mathLibrary

import ru.command.mephi12.exception.ElGamalDecryptionException
import ru.command.mephi12.exception.ElGamalEncryptionException
import ru.command.mephi12.exception.ElGamalParameterException
import java.math.BigInteger

/**
 * Utility class for ElGamal cryptosystem operations
 */
object ElGamalUtils {

    /**
     * ElGamal key pair
     */
    data class KeyPair(
        val p: BigInteger,  // Prime modulus
        val g: BigInteger,  // Generator
        val x: BigInteger,  // Private key
        val y: BigInteger   // Public key (g^x mod p)
    )

    /**
     * ElGamal ciphertext
     */
    data class Ciphertext(
        val c1: BigInteger,  // g^k mod p
        val c2: BigInteger   // m * y^k mod p
    )

    /**
     * Generates an ElGamal key pair
     */
    fun generateKeyPair(bitLength: Int): KeyPair {
        try {
            // Generate a prime p
            val p = PrimeNumbers.generatePrime(bitLength)
    
            // Find a generator of Z_p*
            val g = GroupTheory.findGenerator(p)
    
            // Choose a random private key (1 < x < p-1)
            val x = CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                p.subtract(BigInteger.ONE)
            )
    
            // Compute public key y = g^x mod p
            val y = ModularArithmetic.modPow(g, x, p)
    
            return KeyPair(p, g, x, y)
        } catch (e: Exception) {
            throw ElGamalParameterException("Ошибка при генерации ключей: ${e.message}")
        }
    }

    /**
     * Encrypts a message using ElGamal
     */
    fun encrypt(
        message: BigInteger,
        p: BigInteger,
        g: BigInteger,
        y: BigInteger,
        k: BigInteger? = null
    ): Ciphertext {
        try {
            // Ensure message is valid
            if (message < BigInteger.ZERO || message >= p) {
                throw ElGamalParameterException("Сообщение должно быть между 0 и p-1")
            }
    
            // Generate random k if not provided
            val ephemeralKey = k ?: CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                p.subtract(BigInteger.ONE)
            )
    
            // Compute c1 = g^k mod p
            val c1 = ModularArithmetic.modPow(g, ephemeralKey, p)
    
            // Compute c2 = m * y^k mod p
            val yk = ModularArithmetic.modPow(y, ephemeralKey, p)
            val c2 = ModularArithmetic.modMultiply(message, yk, p)
    
            return Ciphertext(c1, c2)
        } catch (e: ElGamalParameterException) {
            throw e
        } catch (e: Exception) {
            throw ElGamalEncryptionException("Ошибка при шифровании: ${e.message}")
        }
    }

    /**
     * Decrypts an ElGamal ciphertext
     */
    fun decrypt(
        ciphertext: Ciphertext,
        p: BigInteger,
        x: BigInteger
    ): BigInteger {
        try {
            // Compute s = c1^x mod p
            val s = ModularArithmetic.modPow(ciphertext.c1, x, p)
    
            // Compute s^(-1) mod p
            val sInverse = ModularArithmetic.modInverse(s, p)
                ?: throw ElGamalDecryptionException("Невозможно вычислить мультипликативно обратный элемент")
    
            // Compute m = c2 * s^(-1) mod p
            return ModularArithmetic.modMultiply(ciphertext.c2, sInverse, p)
        } catch (e: ElGamalDecryptionException) {
            throw e
        } catch (e: Exception) {
            throw ElGamalDecryptionException("Ошибка при расшифровании: ${e.message}")
        }
    }

    /**
     * Creates a complete demonstration example
     */
    fun createDemoExample(
        bitLength: Int = 64,
        message: BigInteger? = null
    ): ElGamalDemo {
        try {
            // Generate key pair
            val keyPair = generateKeyPair(bitLength)
    
            // Use provided message or generate one
            val m = message ?: CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                keyPair.p
            )
    
            // Choose random k for encryption
            val k = CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                keyPair.p.subtract(BigInteger.ONE)
            )
    
            // Encrypt message
            val yk = ModularArithmetic.modPow(keyPair.y, k, keyPair.p)
            val c1 = ModularArithmetic.modPow(keyPair.g, k, keyPair.p)
            val c2 = ModularArithmetic.modMultiply(m, yk, keyPair.p)
    
            // Decrypt message
            val s = ModularArithmetic.modPow(c1, keyPair.x, keyPair.p)
            val sInverse = ModularArithmetic.modInverse(s, keyPair.p)
                ?: throw ElGamalDecryptionException("Невозможно вычислить мультипликативно обратный элемент")
            val decrypted = ModularArithmetic.modMultiply(c2, sInverse, keyPair.p)
    
            return ElGamalDemo(
                p = keyPair.p,
                g = keyPair.g,
                x = keyPair.x,
                m = m,
    
                y = keyPair.y,
                yCalculation = "${keyPair.g}^${keyPair.x} mod ${keyPair.p} = ${keyPair.y}",
    
                k = k,
                c1 = c1,
                c1Calculation = "${keyPair.g}^$k mod ${keyPair.p} = $c1",
                yk = yk,
                c2 = c2,
                c2Calculation = "$m * $yk mod ${keyPair.p} = $c2",
    
                s = s,
                sInverse = sInverse,
                decryptedMessage = decrypted,
                decryptionCalculation = "$c2 * $sInverse mod ${keyPair.p} = $decrypted"
            )
        } catch (e: ElGamalParameterException) {
            throw e
        } catch (e: ElGamalDecryptionException) {
            throw e
        } catch (e: Exception) {
            throw ElGamalEncryptionException("Ошибка при создании демонстрационного примера: ${e.message}")
        }
    }

    /**
     * Generates a task for students
     */
    fun generateTask(bitLength: Int = 32): ElGamalTask {
        try {
            // Generate smaller numbers for educational purposes
            val keyPair = generateKeyPair(bitLength)
    
            // Generate a message smaller than p
            val m = CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                keyPair.p
            )
    
            return ElGamalTask(
                p = keyPair.p,
                g = keyPair.g,
                x = keyPair.x,
                m = m
            )
        } catch (e: ElGamalParameterException) {
            throw e
        } catch (e: Exception) {
            throw ElGamalParameterException("Ошибка при генерации задания: ${e.message}")
        }
    }

    /**
     * Verifies a student's solution to an ElGamal problem
     */
    fun verifySolution(
        task: ElGamalTask,
        solution: ElGamalSolution
    ): ElGamalVerificationResult {
        try {
            // Verify each step
            val correctY = ModularArithmetic.modPow(task.g, task.x, task.p)
            if (solution.y != correctY) {
                return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Некорректный открытый ключ y. Должно быть $correctY"
                )
            }
    
            val correctC1 = ModularArithmetic.modPow(task.g, solution.k, task.p)
            if (solution.c1 != correctC1) {
                return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Некорректное значение c1. Должно быть $correctC1"
                )
            }
    
            val yk = ModularArithmetic.modPow(solution.y, solution.k, task.p)
            val correctC2 = ModularArithmetic.modMultiply(task.m, yk, task.p)
            if (solution.c2 != correctC2) {
                return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Некорректное значение c2. Должно быть $correctC2"
                )
            }
    
            val s = ModularArithmetic.modPow(solution.c1, task.x, task.p)
            val sInverse = ModularArithmetic.modInverse(s, task.p)
                ?: return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Невозможно вычислить мультипликативно обратный элемент"
                )
    
            val correctDecrypted = ModularArithmetic.modMultiply(solution.c2, sInverse, task.p)
            if (solution.decryptedMessage != correctDecrypted) {
                return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Некорректное расшифрованное сообщение. Должно быть $correctDecrypted"
                )
            }
    
            // Check if decrypted message matches original
            if (solution.decryptedMessage != task.m) {
                return ElGamalVerificationResult(
                    isCorrect = false,
                    errorMessage = "Расшифрованное сообщение не совпадает с исходным: ${task.m}"
                )
            }
    
            return ElGamalVerificationResult(isCorrect = true)
        } catch (e: Exception) {
            throw ElGamalParameterException("Ошибка при проверке решения: ${e.message}")
        }
    }

    /**
     * Computes the solution for a given task
     */
    fun solveDemoTask(task: ElGamalTask, k: BigInteger? = null): ElGamalSolution {
        try {
            // Compute public key
            val y = ModularArithmetic.modPow(task.g, task.x, task.p)
    
            // Choose k or use provided value
            val ephemeralKey = k ?: CryptoRandom.randomBigInteger(
                BigInteger.ONE,
                task.p.subtract(BigInteger.ONE)
            )
    
            // Encrypt the message
            val ciphertext = encrypt(task.m, task.p, task.g, y, ephemeralKey)
    
            // Decrypt to verify
            val decrypted = decrypt(ciphertext, task.p, task.x)
    
            return ElGamalSolution(
                y = y,
                k = ephemeralKey,
                c1 = ciphertext.c1,
                c2 = ciphertext.c2,
                decryptedMessage = decrypted
            )
        } catch (e: ElGamalParameterException) {
            throw e
        } catch (e: ElGamalEncryptionException) {
            throw e
        } catch (e: ElGamalDecryptionException) {
            throw e
        } catch (e: Exception) {
            throw ElGamalParameterException("Ошибка при решении задания: ${e.message}")
        }
    }
}