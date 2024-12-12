package ru.command.mephi12.service.impl.problems.backpack


import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.command.mephi12.dto.*
import ru.command.mephi12.exception.AppException
import java.math.BigInteger
import kotlin.math.abs

@Service
@Qualifier(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING)
class BackpackSuperIncreasingProblemSolverServiceImpl : AbstractBackpackProblemSolverService() {
    /**
     * Корректирует лёгкий рюкзак, делая его строго сверхвозрастающим и нужной длины.
     * Заменяет нули на случайные числа >1 и добавляет новые элементы при необходимости.
     */
    override fun fixLightBackpack(input: List<BigInteger>, size: Int, p: Int): List<String> {
        val result = input.toMutableList()
        var sumSoFar = BigInteger.ZERO

        for (i in result.indices) {
            if (i >= size) break // Не обрабатываем лишние элементы здесь
            if (result[i] == BigInteger.ZERO || result[i] <= sumSoFar) {
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
                return trimmed.map { it.toString() }
            } else {
                throw AppException("Лёгкий рюкзак содержит больше элементов, чем необходимо, и не может быть обрезан без нарушения сверхвозрастающей последовательности.")
            }
        }

        // Ещё раз проверим сверхвозрастающую последовательность
        if (!isSuperIncreasing(result)) {
            throw AppException("Не удалось привести лёгкий рюкзак к сверхвозрастающей последовательности.")
        }

        return result.map { it.toString() }
    }

    /**
     * Генерация следующего числа для сверхвозрастающей последовательности.
     */
    private fun generateNextSuperIncreasingNumber(currentSum: BigInteger): BigInteger {
        // Выбираем число больше текущей суммы. Добавляем случайное число от 1 до 10 для разнообразия.
        val addition = BigInteger((1 + abs(random.nextInt(10))).toString())
        return currentSum + addition
    }

    /**
     * Проверка, является ли последовательность строго сверхвозрастающей.
     */
    private fun isSuperIncreasing(sequence: List<BigInteger>): Boolean {
        var sum = BigInteger.ZERO
        for (value in sequence) {
            if (value <= sum) return false
            sum += value
        }
        return true
    }
}
