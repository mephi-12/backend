package ru.command.mephi12.service.problems.backpack

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import ru.command.mephi12.dto.*
import java.math.BigInteger

@Service
@Qualifier(BackpackProblemTypeQualifier.CODE_DEGREES)
class BackpackDegreesProblemSolverServiceImpl : AbstractBackpackProblemSolverService() {
    /**
     * Формирование части лёгкого рюкзака для "степеней по разрядам":
     * Каждый элемент вида: [нулевые прогалы][p^i]
     * zeroCount = длина числа (p^n - 1)/(p-1), где n = size
     */
    override fun fixLightBackpack(input: List<BigInteger>, size: Int, p: Int): List<String> {
        // Считаем сумму геом. прогрессии: S = (p^n - 1)/(p-1)
        // Если p=1, это другой случай, но p>1 по условию
        val S = geometricSum(p, size)
        val zeroCount = S.toString().length

        val result = mutableListOf<String>()
        var sumSoFar = BigInteger.ZERO

        // Для каждой степени p^i формируем число
        for (i in 0 until size) {
            var found = false
            val pPow = powLong(BigInteger(p.toString()), i)

            while (!found) {
                // Формируем кандидат:
                // candidateStr = "0".repeat(zeroCount) + p^i
                // randomPart и p^i соединяем как строки
                val candidateStr = "0".repeat(zeroCount) + pPow.toString()
                val candidate = BigInteger(candidateStr)

                if (candidate > sumSoFar) {
                    result.add(candidateStr)
                    sumSoFar += candidate
                    found = true
                }
            }
        }

        return result
    }

    /**
     * Геометрическая сумма: (p^n - 1)/(p - 1)
     */
    private fun geometricSum(p: Int, n: Int): BigInteger {
        // Быстро считаем p^n
        val pPowN = powLong(BigInteger(p.toString()), n)
        return (pPowN - BigInteger.ONE) / BigInteger((p - 1).toString())
    }
}
