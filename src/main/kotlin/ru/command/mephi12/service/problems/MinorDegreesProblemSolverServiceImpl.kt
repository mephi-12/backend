package ru.command.mephi12.service.problems

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.command.mephi12.dto.*
import ru.command.mephi12.service.ProblemSolverService
import java.util.*
import kotlin.math.ceil


@Service
@Qualifier(ProblemTypeQualifier.MINOR_DEGREES)
class MinorDegreesProblemSolverServiceImpl : ProblemSolverService {

    private val random = java.util.Random()

    // Первые 20 простых чисел
    private val first20Primes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    )

    override fun solve(request: ProblemRequest): ProblemResponse {
        return when (request.type) {
            ProblemType.MINOR_DEGREES -> solveMinorDegrees(request)
            else -> throw AppException("Тип задачи ${request.type} не поддерживается.")
        }
    }

    private fun solveMinorDegrees(request: ProblemRequest): ProblemResponse {
        // 1. Определяем p (power)
        val p = request.power?.takeIf { it > 1 } ?: first20Primes[random.nextInt(first20Primes.size)]

        // 2. Генерируем сообщение, если пустое
        val message = request.message.ifEmpty {
            generateRandomMessage()
        }

        // 3. Формируем лёгкий рюкзак
        val lightBackpack = fixLightBackpack(message.size, p)

        // 4. Вычисляем модуль
        val module = lightBackpack.sum() + 1

        // 5. Определяем омегу
        val omega = request.omega?.let {
            if (gcd(it, module) != 1L) {
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

        return ProblemResponse(
            power = p,
            type = request.type,
            message = message,
            lightBackpack = lightBackpack,
            omega = omega,
            hardBackpack = hardBackpack,
            encodedMessage = encodedSum,
            module = module.toString(),
            reverseOmega = reverseOmega
        )
    }

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

    /**
     * Формирование лёгкого рюкзака для "младших степеней":
     * Каждый элемент вида: [randomPart][нулевые прогалы][p^i]
     * zeroCount = длина числа (p^n - 1)/(p-1), где n = size
     */
    private fun fixLightBackpack(size: Int, p: Int): List<Long> {
        // Считаем сумму геом. прогрессии: S = (p^n - 1)/(p-1)
        // Если p=1, это другой случай, но p>1 по условию
        val S = geometricSum(p, size)
        val zeroCount = S.toString().length

        val result = mutableListOf<Long>()
        var sumSoFar = 0L

        // Для каждой степени p^i формируем число
        for (i in 0 until size) {
            var found = false
            var randomPart = 1
            val pPow = powLong(p.toLong(), i)

            while (!found) {
                // Формируем кандидат:
                // candidateStr = randomPart + "0".repeat(zeroCount) + p^i
                // randomPart и p^i соединяем как строки
                val candidateStr = randomPart.toString() + "0".repeat(zeroCount) + pPow.toString()
                val candidate = candidateStr.toLong()

                if (candidate > sumSoFar) {
                    result.add(candidate)
                    sumSoFar += candidate
                    found = true
                } else {
                    // Увеличиваем randomPart для более крупного числа
                    randomPart++
                    // Теоретически можно сделать ограничение, если слишком долго не находим, но на практике должно быстро находиться.
                }
            }
        }

        // Проверка сверхвозрастающей последовательности
        if (!isSuperIncreasing(result)) {
            throw AppException("Не удалось привести рюкзак к сверхвозрастающей последовательности. Результат: $result")
        }

        return result
    }

    private fun isSuperIncreasing(sequence: List<Long>): Boolean {
        var sum = 0L
        for (value in sequence) {
            if (value <= sum) return false
            sum += value
        }
        return true
    }

    private fun powLong(base: Long, exp: Int): Long {
        var result = 1L
        repeat(exp) {
            result *= base
        }
        return result
    }

    /**
     * Геометрическая сумма: (p^n - 1)/(p - 1)
     */
    private fun geometricSum(p: Int, n: Int): Long {
        // Быстро считаем p^n
        val pPowN = powLong(p.toLong(), n)
        return (pPowN - 1) / (p - 1)
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = a
        var y = b
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }

    private fun generateCoprime(m: Long): Long {
        var omega: Long
        var attempts = 0
        do {
            if (attempts > 1000) throw AppException("Не удалось сгенерировать взаимно простую омегу с модулем $m после 1000 попыток.")
            omega = (2..(m - 1)).random().toLong()
            attempts++
        } while (gcd(omega, m) != 1L)
        return omega
    }

    private fun ClosedRange<Long>.random() =
        (start + random.nextLong().mod(endInclusive - start + 1)).coerceAtLeast(start)

    private fun modInverse(a: Long, m: Long): Long? {
        var aVar = a
        var m0 = m
        var x0 = 0L
        var x1 = 1L

        if (m == 1L) return null

        while (aVar > 1) {
            val q = aVar / m0
            var temp = m0
            m0 = aVar % m0
            aVar = temp
            temp = x0
            x0 = x1 - q * x0
            x1 = temp
        }

        if (x1 < 0) x1 += m

        return if (aVar == 1L) x1 else null
    }

    private fun encodeMessage(message: List<Boolean>, hardBackpack: List<Long>): Long {
        return message.zip(hardBackpack).sumOf { if (it.first) it.second else 0L }
    }
}
