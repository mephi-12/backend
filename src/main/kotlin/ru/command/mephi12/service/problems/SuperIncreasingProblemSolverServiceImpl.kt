package ru.command.mephi12.service.problems


import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.command.mephi12.dto.*
import ru.command.mephi12.service.ProblemSolverService
import java.util.Random
import kotlin.math.abs
import kotlin.math.ceil

@Service
@Qualifier(ProblemTypeQualifier.SUPER_INCREASING)
class SuperIncreasingProblemSolverServiceImpl : ProblemSolverService {

    private val random = Random()

    override fun solve(request: ProblemRequest): ProblemResponse {
        return when (request.type) {
            ProblemType.SUPER_INCREASING -> solveSuperIncreasing(request)
            else -> throw AppException("Тип задачи ${request.type} не поддерживается.")
        }
    }

    private fun solveSuperIncreasing(request: ProblemRequest): ProblemResponse {
        // 1. Определяем сообщение
        val message = request.message.ifEmpty {
            generateRandomMessage()
        }

        // 2. Обрабатываем лёгкий рюкзак:
        // Делаем его длиной, равной длине сообщения, и строго сверхвозрастающим
        val lightBackpack = fixLightBackpack(request.lightBackpack, message.size)

        // 3. Вычисляем модуль
        val module = lightBackpack.sum() + 1

        // 4. Определяем омегу
        val omega = request.omega?.let {
            if (gcd(it, module) != 1L) {
                throw AppException("Омега должна быть взаимно простой с модулем. Получено: omega=$it, module=$module.")
            }
            it
        } ?: generateCoprime(module)

        // 5. Вычисляем публичный рюкзак
        val hardBackpack = lightBackpack.map { (it * omega) % module }

        // 6. Ищем обратный элемент к омеге по модулю
        val reverseOmega = modInverse(omega, module)
            ?: throw AppException("Не удалось найти обратный элемент для омеги $omega по модулю $module.")

        // 7. Шифрование сообщения
        val encodedSum = encodeMessage(message, hardBackpack)

        // Формируем ответ
        return ProblemResponse(
            power = request.power,
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

    /**
     * Генерация случайного сообщения длиной от 8 до 16 бит с не менее 1/3 нулей.
     */
    private fun generateRandomMessage(): List<Boolean> {
        val length = random.nextInt(9) + 8 // от 8 до 16
        val result = MutableList(length) { random.nextBoolean() }

        // Проверяем долю нулей - минимум 1/3
        val zerosCount = result.count { !it }
        val requiredZeros = ceil(length / 3.0).toInt()
        if (zerosCount < requiredZeros) {
            val needed = requiredZeros - zerosCount
            // Заменяем часть true на false
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
     * Корректирует лёгкий рюкзак, делая его строго сверхвозрастающим и нужной длины.
     * Заменяет нули на случайные числа >1 и добавляет новые элементы при необходимости.
     */
    private fun fixLightBackpack(input: List<Long>, size: Int): List<Long> {
        val result = input.toMutableList()
        var sumSoFar = 0L

        for (i in result.indices) {
            if (i >= size) break // Не обрабатываем лишние элементы здесь
            if (result[i] == 0L || result[i] <= sumSoFar) {
                result[i] = generateNextSuperIncreasingNumber(sumSoFar)
            }
            sumSoFar += result[i]
        }

        // Если длина меньше нужной - добавляем элементы
        while (result.size < size) {
            val next = generateNextSuperIncreasingNumber(sumSoFar)
            result.add(next)
            sumSoFar += next
        }

        // Если длина больше нужной, проверяем и обрабатываем
        if (result.size > size) {
            // Проверяем, что первые 'size' элементов являются супервозрастающей последовательностью
            val trimmed = result.take(size)
            if (isSuperIncreasing(trimmed)) {
                return trimmed
            } else {
                throw AppException("Лёгкий рюкзак содержит больше элементов, чем необходимо, и не может быть обрезан без нарушения сверхвозрастающей последовательности.")
            }
        }

        // Ещё раз проверим сверхвозрастающую последовательность
        if (!isSuperIncreasing(result)) {
            throw AppException("Не удалось привести лёгкий рюкзак к сверхвозрастающей последовательности.")
        }

        return result
    }

    /**
     * Генерация следующего числа для сверхвозрастающей последовательности.
     */
    private fun generateNextSuperIncreasingNumber(currentSum: Long): Long {
        // Выбираем число больше текущей суммы. Добавляем случайное число от 1 до 10 для разнообразия.
        val addition = 1 + abs(random.nextInt(10))
        return currentSum + addition
    }

    /**
     * Проверка, является ли последовательность строго сверхвозрастающей.
     */
    private fun isSuperIncreasing(sequence: List<Long>): Boolean {
        var sum = 0L
        for (value in sequence) {
            if (value <= sum) return false
            sum += value
        }
        return true
    }

    /**
     * Вычисление наибольшего общего делителя (НОД) двух чисел.
     */
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

    /**
     * Генерация случайного числа, взаимно простого с модулем.
     */
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

    /**
     * Нахождение обратного элемента по модулю с использованием расширенного алгоритма Евклида.
     */
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

    /**
     * Шифрование сообщения: вычисление суммы элементов публичного рюкзака, соответствующих установленным битам.
     */
    private fun encodeMessage(message: List<Boolean>, hardBackpack: List<Long>): Long {
        return hardBackpack.mapIndexed { index: Int, l: Long -> if (message[index]) l else 0 }.sum()
    }

    /**
     * Преобразование зашифрованной суммы обратно в список булевых значений.
     * Здесь предполагается, что это шаг декодирования, но поскольку это часть шифрования,
     * возможно, потребуется уточнение требований.
     */
    private fun encodeToBooleanList(encodedSum: Long, module: Long): List<Boolean> {
        // В данном контексте, возможно, лучше оставить encodedMessage как числовое значение.
        // Однако, следуя вашей структуре, преобразуем сумму обратно в битовый список.
        val bits = mutableListOf<Boolean>()
        var value = encodedSum
        while (value > 0) {
            bits.add((value % 2) == 1L)
            value /= 2
        }
        // Дополняем до размера модуля, если необходимо (опционально)
        return bits.reversed()
    }
}
