package ru.command.mephi12.service.impl.problems.backpack

import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemEditorialResponse
import ru.command.mephi12.service.BackpackProblemSolverService
import ru.command.mephi12.utils.generateCoprime
import ru.command.mephi12.utils.generateRandomMessage
import ru.command.mephi12.utils.generateRandomPartOfLightBackpack
import ru.command.mephi12.utils.sum
import java.math.BigInteger
import java.util.Random

abstract class AbstractBackpackProblemSolverService: BackpackProblemSolverService {

    protected val random = Random()

    // Первые 20 простых чисел
    private val first20Primes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    )

    override fun solve(request: BackpackProblemEditorialRequest): BackpackProblemEditorialResponse {
        // 1. Определяем p (power)
        val p = request.power?.takeIf { it > 1 } ?: first20Primes[random.nextInt(first20Primes.size)]

        // 2. Генерируем сообщение, если пустое
        val message = request.message.ifEmpty {
            generateRandomMessage()
        }

        // 3. Обрабатываем лёгкий рюкзак, делаем его длиной m
        val lightBackpack = request.lightBackpack.takeIf { it.isNotEmpty() } ?: generateRandomPartOfLightBackpack(fixLightBackpack(request.lightBackpack, message.size, p))
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
        return BackpackProblemEditorialResponse(
            power = if(request.type == ProblemType.BACKPACK_CODE_DEGREES) p else null,
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