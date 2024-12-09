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
        val result = mutableListOf<String>()
        var sumSoFar = BigInteger.ZERO

        // Для каждой степени p^i формируем число
        for (i in 0 until size) {
            var found = false
            val pPow = powLong(BigInteger(p.toString()), i).toString()

            while (!found) {
                // Формируем кандидат:
                val candidate = BigInteger(pPow)

                if (candidate > sumSoFar) {
                    result.add(candidate.toString())
                    sumSoFar += candidate
                    found = true
                }
            }
        }

        return result
    }
}
