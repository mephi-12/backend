package ru.command.mephi12.service.impl.problems.backpack

import org.springframework.stereotype.Service
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.dto.backdoor.BackpackBackdoorProblemEditorialRequest
import ru.command.mephi12.dto.backdoor.BackpackBackdoorProblemEditorialResponse
import ru.command.mephi12.exception.AppException
import java.math.BigInteger
import java.security.SecureRandom

@Service
class BackpackBackDoorDegreesProblemSolverServiceImpl {
    private val random = SecureRandom()
    private val fallbackPrimes = listOf(
        3, 5, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    )

    fun getEditorialTask(request: BackpackBackdoorProblemEditorialRequest): BackpackBackdoorProblemEditorialResponse {
        repeat(500) {
            try {
                return tryGenerate(request)
            } catch (_: Exception) {}
        }
        throw AppException("Не смогли сгенерировать пример спустя 500 попыток")
    }

    private fun tryGenerate(request: BackpackBackdoorProblemEditorialRequest)
            : BackpackBackdoorProblemEditorialResponse {
        // 1. Выбор основания p >= 3
        val p = request.power?.takeIf { it >= 3 } ?: fallbackPrimes.random()

        // 2. Подготовка сообщения: минимум длина 4
        var message = request.message
        if (message.size < 4) {
            message = List(4) { random.nextBoolean() }
        }

        // 3. Определяем ell на основе длины сообщения и будущих строк
        val ellByMsg = (message.size + 1) / 2

        // 4. Генерируем секретные строки omegaStr и moduleStr одинаковой длины
        //    длина выбирается как max(ellByMsg, длина случайного omega, длина случайного module)
        // 4.1. Случайный moduleStr длины >= ellByMsg
        val ellTarget = ellByMsg.coerceAtLeast(1)
        val moduleValRaw = generateRandomBetween(
            BigInteger.TEN.pow(ellTarget - 1),
            BigInteger.TEN.pow(ellTarget).subtract(BigInteger.ONE)
        )
        // 4.2. Случайная omegaVal взаимно простая с moduleValRaw
        var omegaValRaw: BigInteger
        do {
            omegaValRaw = generateRandomBetween(
                BigInteger.ONE,
                moduleValRaw.subtract(BigInteger.ONE)
            )
        } while (omegaValRaw.gcd(moduleValRaw) != BigInteger.ONE)
        val rawOmegaStr = omegaValRaw.toString()
        val rawModStr   = moduleValRaw.toString()
        val ell = maxOf(ellByMsg, rawOmegaStr.length, rawModStr.length)
        val omegaStr  = rawOmegaStr.padStart(ell, '0')
        val moduleStr = rawModStr  .padStart(ell, '0')
        val moduleVal = BigInteger(moduleStr)
        val omegaVal  = BigInteger(omegaStr)

        // 5. Подгоняем длину сообщения до 2*ell
        val m = 2 * ell
        if (message.size < m) {
            val pad = List(m - message.size) { false }
            message = message + pad
        }

        // 6. Формируем строку секретных цифр и ищем минимальный spaceSize
        val secretStr = omegaStr + moduleStr
        val lightBackpack = mutableListOf<BigInteger>()
        var foundSpace: Int? = null
        val ten = BigInteger.TEN
        for (spaceSz in 0..64) {
            lightBackpack.clear()
            var ok = true
            val multiplier = ten.pow(spaceSz + 1)
            for (i in 0 until m) {
                val prefix = BigInteger.valueOf(p.toLong()).pow(i)
                val targetDigit = secretStr[i].digitToInt()
                var liFound: BigInteger? = null
                for (rDigit in 0..9) {
                    val Li = prefix.multiply(multiplier).add(BigInteger.valueOf(rDigit.toLong()))
                    val Hi = Li.multiply(omegaVal).mod(moduleVal)
                    if (Hi.mod(ten).toInt() == targetDigit) {
                        liFound = Li
                        break
                    }
                }
                if (liFound == null) {
                    ok = false
                    break
                }
                lightBackpack.add(liFound)
            }
            if (ok) {
                foundSpace = spaceSz
                break
            }
        }
        if (foundSpace == null) {
            throw AppException("Не удалось встроить backdoor при spaceSize ≤64")
        }

        // 7. Публичный (тяжёлый) рюкзак
        val hardBackpack = lightBackpack.map { it.multiply(omegaVal).mod(moduleVal) }

        // 8. Обратный элемент омеги
        val reverseOmega = modInverse(omegaVal, moduleVal)
            ?: throw AppException("Не удалось найти обратный элемент омеги")

        // 9. Шифрование сообщения
        val encodedMessage = message.zip(hardBackpack).fold(BigInteger.ZERO) { acc, (bit, hi) ->
            acc + if (bit) hi else BigInteger.ZERO
        }

        // 10. Формируем ответ
        return BackpackBackdoorProblemEditorialResponse(
            power = p,
            spaceSize = foundSpace.toLong(),
            message = message,
            lightBackpack = lightBackpack,
            omega = omegaStr,
            hardBackpack = hardBackpack,
            encodedMessage = encodedMessage,
            module = moduleStr,
            reverseOmega = reverseOmega
        )
    }

    // Генерация случайного BigInteger в диапазоне [min, max]
    private fun generateRandomBetween(min: BigInteger, max: BigInteger): BigInteger {
        val range = max.subtract(min).add(BigInteger.ONE)
        var result: BigInteger
        do {
            result = BigInteger(range.bitLength(), random)
        } while (result >= range)
        return result.add(min)
    }

    // Алгоритм Евклида — поиск обратного по модулю
    private fun modInverse(a: BigInteger, m: BigInteger): BigInteger? {
        var aa = a.mod(m)
        var mm = m
        var x0 = BigInteger.ZERO
        var x1 = BigInteger.ONE
        if (mm == BigInteger.ONE) return null
        while (aa > BigInteger.ONE) {
            val q = aa.divide(mm)
            var t = mm
            mm = aa.mod(mm)
            aa = t
            t = x0
            x0 = x1.subtract(q.multiply(x0))
            x1 = t
        }
        if (x1 < BigInteger.ZERO) x1 = x1.add(m)
        return if (aa == BigInteger.ONE) x1 else null
    }
}