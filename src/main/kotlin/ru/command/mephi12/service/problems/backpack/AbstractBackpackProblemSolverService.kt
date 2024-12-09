package ru.command.mephi12.service.problems.backpack

import ru.command.mephi12.exception.AppException
import ru.command.mephi12.dto.BackpackProblemRequest
import ru.command.mephi12.dto.BackpackProblemDto
import ru.command.mephi12.dto.BackpackProblemType
import ru.command.mephi12.service.BackpackProblemSolverService
import ru.command.mephi12.utils.fillUpWithZero
import ru.command.mephi12.utils.sum
import java.math.BigInteger
import java.security.SecureRandom
import java.util.Random
import kotlin.math.ceil

abstract class AbstractBackpackProblemSolverService: BackpackProblemSolverService {

    protected val random = Random()

    // Первые 20 простых чисел
    private val first20Primes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    )

    override fun solve(request: BackpackProblemRequest): BackpackProblemDto {
        // 1. Определяем p (power)
        val p = request.power?.takeIf { it > 1 } ?: first20Primes[random.nextInt(first20Primes.size)]

        // 2. Генерируем сообщение, если пустое
        val message = request.message.ifEmpty {
            generateRandomMessage()
        }

        // 3. Обрабатываем лёгкий рюкзак, делаем его длиной m
        val toAddAtStart = random.nextInt(0, 4)
        val toBeAddedAtStart = List(message.size) { generateRandomNumber(toAddAtStart) }
        val toAddAtEnd = random.nextInt(0, 4)
        val toBeAddedAtEnd = List(message.size) { generateRandomNumber(toAddAtEnd)}.fillUpWithZero()
        val lightBackpack = fixLightBackpack(request.lightBackpack, message.size, p)
            .fillUpWithZero()
            .mapIndexed { index, value -> BigInteger("${toBeAddedAtStart[index]}$value${toBeAddedAtEnd[index]}") }

        // 4. Вычисляем модуль
        val module = lightBackpack.sum() + BigInteger.ONE

        // 5. Определяем омегу
        val omega = request.omega?.let {
            if (gcd(it, module) != BigInteger.ONE) {
                throw AppException("Омега должна быть взаимно простой с модулем. Получено: omega=$it, module=$module.")
            }
            it
        } ?: generateCoprime(module)

        // 6. Публичный рюкзак
        val hardBackpack = lightBackpack.map { (it * omega) % module }

        // 7. Обратный элемент к омеге
        val reverseOmega = modInverse(omega, module)
            ?: throw AppException("Не удалось найти обратный элемент для омеги $omega по модулю $module.")

        // 8. Шифрование сообщения
        val encodedSum = encodeMessage(message, hardBackpack)

        // Формируем ответ
        return BackpackProblemDto(
            power = if(request.type == BackpackProblemType.CODE_DEGREES) p else null,
            type = request.type,
            message = message,
            lightBackpack = lightBackpack,
            omega = omega,
            hardBackpack = hardBackpack,
            encodedMessage = encodedSum,
            module = module,
            reverseOmega = reverseOmega
        )
    }

    abstract fun fixLightBackpack(input: List<BigInteger>, size: Int, p: Int): List<String>

    protected fun powLong(base: BigInteger, exp: Int): BigInteger {
        var result = BigInteger.ONE
        repeat(exp) {
            result *= base
        }
        return result
    }

    private fun generateRandomNumber(size: Int) : String =
        (0 until size)
            .map { ('0' until '9').random() }
            .joinToString(separator = "")

    private fun generateRandomMessage(): List<Boolean> {
        val length = random.nextInt(9) + 8 // от 8 до 16
        val result = MutableList(length) { random.nextBoolean() }

        // Проверяем долю нулей - минимум 1/3
        val zerosCount = result.count { !it }
        val requiredZeros = ceil(length / 3.0).toInt()
        if (zerosCount < requiredZeros) {
            val needed = requiredZeros - zerosCount
            var replaced = 0
            for (i in result.indices) {
                if (replaced >= needed) break
                if (result[i]) {
                    result[i] = false
                    replaced++
                }
            }
        }
        return result
    }

    private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
        var x = a
        var y = b
        while (y != BigInteger.ZERO) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }

    private fun generateCoprime(m: BigInteger): BigInteger {
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

    private fun modInverse(a: BigInteger, m: BigInteger): BigInteger? {
        var aVar = a
        var m0 = m
        var x0 = BigInteger.ZERO
        var x1 = BigInteger.ONE

        if (m == BigInteger.ONE) return null

        while (aVar > BigInteger.ONE) {
            val q = aVar / m0
            var temp = m0
            m0 = aVar % m0
            aVar = temp
            temp = x0
            x0 = x1 - q * x0
            x1 = temp
        }

        if (x1 < BigInteger.ZERO) x1 += m

        return if (aVar == BigInteger.ONE) x1 else null
    }

    private fun encodeMessage(message: List<Boolean>, hardBackpack: List<BigInteger>): BigInteger =
        message.zip(hardBackpack).sumOf { if (it.first) it.second else BigInteger.ZERO }
}