package ru.command.mephi12.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import ru.command.mephi12.constants.EL_GAMAL_QUALIFIER
import ru.command.mephi12.dto.ProblemSubmitResponse
import ru.command.mephi12.service.ElGamalService
import ru.command.mephi12.service.ProblemsCheckerService
import ru.command.mephi12.utils.mathLibrary.*
import java.math.BigInteger

@Service(EL_GAMAL_QUALIFIER)
class ElGamalServiceImpl(
    val objectMapper: ObjectMapper,
) : ElGamalService, ProblemsCheckerService<ElGamalTask> {

    /**
     * Создает демонстрационный пример работы криптосистемы Эль-Гамаля
     * с подробными пояснениями для каждого шага
     */
    override fun generateDemo(bitLength: Int, message: BigInteger?): ElGamalDemo {
        return ElGamalUtils.createDemoExample(bitLength, message)
    }

    /**
     * Создает задание для пользователя с заданной длиной ключа
     */
    override fun generateTask(bitLength: Int): ElGamalTask {
        return ElGamalUtils.generateTask(bitLength)
    }
    
    /**
     * Проверяет решение задания пользователя
     * Возвращает результат проверки с сообщением об ошибке в случае некорректного решения
     */
    override fun checkSolution(task: ElGamalTask, solution: ElGamalSolution): ElGamalVerificationResult {
        return ElGamalUtils.verifySolution(task, solution)
    }
    
    /**
     * Шифрует сообщение с использованием криптосистемы Эль-Гамаля
     */
    override fun encrypt(message: BigInteger, p: BigInteger, g: BigInteger, y: BigInteger, k: BigInteger?): ElGamalUtils.Ciphertext {
        return ElGamalUtils.encrypt(message, p, g, y, k)
    }
    
    /**
     * Расшифровывает сообщение с использованием криптосистемы Эль-Гамаля
     */
    override fun decrypt(ciphertext: ElGamalUtils.Ciphertext, p: BigInteger, x: BigInteger): BigInteger {
        return ElGamalUtils.decrypt(ciphertext, p, x)
    }

    override fun check(statement: String, solutionRequest: String): ProblemSubmitResponse {
        val task = objectMapper.readValue(statement, ElGamalTask::class.java)
        val solution = objectMapper.readValue(statement, ElGamalSolution::class.java)
        val elGamalVerdict = checkSolution(task, solution)
        return ProblemSubmitResponse(
            isOk = elGamalVerdict.isCorrect,
            errorMessage = elGamalVerdict.errorMessage,
        )
    }

    override fun generateProblem(): ElGamalTask = generateTask(5)
}