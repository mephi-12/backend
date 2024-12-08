package ru.command.mephi12.service.problems

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.command.mephi12.dto.*
import ru.command.mephi12.service.ProblemSolverService
import kotlin.math.abs

@Service
@Qualifier(ProblemTypeQualifier.MAJOR_DEGREES)
class MajorDegreesProblemSolverServiceImpl : ProblemSolverService {

    private val random = java.util.Random()

    // Первые 20 простых чисел
    private val first20Primes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    )

    override fun solve(request: ProblemRequest): ProblemResponse {
        return when (request.type) {
            ProblemType.MAJOR_DEGREES -> solveMajorDegrees(request)
            else -> throw AppException("Тип задачи ${request.type} не поддерживается.")
        }
    }

    private fun solveMajorDegrees(request: ProblemRequest): ProblemResponse {
        // 1. Определяем p (power)
        val p = request.power?.takeIf { it > 1 } ?: first20Primes[random.nextInt(first20Primes.size)]

        // 2. Генерируем сообщение, если пустое
        val message = request.message.ifEmpty {
            generateRandomMessage()
        }

        // 3. Обрабатываем лёгкий рюкзак, делаем его длиной m
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

        // Формируем ответ
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
        val requiredZeros = kotlin.math.ceil(length / 3.0).toInt()
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

    private fun fixLightBackpack(size: Int, p: Int): List<Long> {
        val zeroCount = (9 * size).toString().length
        val result = mutableListOf<Long>()
        var sumSoFar = 0L
        var k = 1

        val random = kotlin.random.Random

        for (i in 0 until size) {
            var found = false
            // Увеличиваем k, пока не найдём число больше sumSoFar
            // Чем дальше, тем больше k нам понадобится, т.к. sumSoFar растёт
            var localK = k
            while (!found) {
                val pPow = powLong(p.toLong(), localK)
                // Формируем число: [p^k][zeroCount нулей][рандомная цифра]
                val randomDigit = (1..9).random(random) // Цифра от 1 до 9
                val candidateStr = pPow.toString() + "0".repeat(zeroCount) + randomDigit.toString()
                val candidate = candidateStr.toLong()

                if (candidate > sumSoFar) {
                    result.add(candidate)
                    sumSoFar += candidate
                    found = true
                } else {
                    // Увеличиваем степень p
                    localK++
                }
            }
            // Чтобы обеспечить рост, следующий элемент попробуем с ещё большей степенью
            k = localK + 1
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
