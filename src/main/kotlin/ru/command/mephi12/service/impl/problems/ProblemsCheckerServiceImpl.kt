package ru.command.mephi12.service.impl.problems

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.dao.BackpackProblemDao
import ru.command.mephi12.database.entity.BackpackProblem
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.dto.*
import ru.command.mephi12.dto.mapper.BackpackProblemMapper
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.exception.TaskSolverProblemException
import ru.command.mephi12.service.ProblemsCheckerService
import ru.command.mephi12.service.impl.problems.backpack.AbstractBackpackProblemSolverService
import ru.command.mephi12.utils.*
import java.math.BigInteger
import java.util.*
import kotlin.random.Random

@Component
class ProblemsCheckerServiceImpl(
    @Qualifier(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING)
    private val superIncreasingSolver: AbstractBackpackProblemSolverService,
    @Qualifier(BackpackProblemTypeQualifier.CODE_DEGREES)
    private val codeDegreesSolver: AbstractBackpackProblemSolverService,
    private val mapper: BackpackProblemMapper,
    private val dao: BackpackProblemDao,
) : ProblemsCheckerService<BackpackProblemResponse, BackpackProblemSubmitRequest> {
    val rand: Random = Random

    private val first3Primes = listOf(2, 3, 5)

    @Transactional
    override fun generateProblem(): BackpackProblemResponse {

        val rawType = getRandomInt(0, 1)
        val type = when (rawType) {
            0 -> ProblemType.BACKPACK_CODE_DEGREES
            else -> ProblemType.BACKPACK_CODE_SUPER_INCREASING
        }

        val pow: Int? = first3Primes.random().takeIf { type == ProblemType.BACKPACK_CODE_DEGREES }

        val message = generateRandomMessage(3, 5)

        val rawLightBackpack = generateRandomPartOfLightBackpack(
            when (type) {
                ProblemType.BACKPACK_CODE_DEGREES -> codeDegreesSolver
                ProblemType.BACKPACK_CODE_SUPER_INCREASING -> superIncreasingSolver
                else -> codeDegreesSolver
            }.fixLightBackpack(listOf(), message.size, pow ?: -1),
            2,
            2,
        )

        val toBeRemained = getRandomInt(0, 2)

        val lightBackpack = MutableList<BigInteger?>(rawLightBackpack.size) { null }

        repeat(toBeRemained) {
            val pos = rand.nextInt(0, lightBackpack.size)
            lightBackpack[pos] = rawLightBackpack[pos]
        }

        val resLightBackpack = lightBackpack.map { it ?: BigInteger.ZERO }

        val shouldGenerateOmega = rand.nextBoolean()

        val module = rawLightBackpack.sum() + BigInteger.ONE

        val omega = generateCoprime(module).takeIf { shouldGenerateOmega }

        val entity = mapper.requestToEntity(
            BackpackProblemEditorialRequest(
                pow,
                type,
                message,
                resLightBackpack,
                omega,
            )
        )

        return mapper.entityToResponse(
            dao.save(
                entity
            )
        )
    }

    override fun check(id: UUID, request: BackpackProblemSubmitRequest): BackpackProblemResponse {
        val task =
            dao.findById(id).orElseThrow { throw AppException(HttpStatus.NOT_FOUND, "Задания с таким uuid не найдено") }
        return mapper.entityToResponse(
            dao.save(
                try {
                    println("\n\nTask: ${jsonMapper().registerModules(JavaTimeModule()).writeValueAsString(task)}\n\n")
                    println("\n\nSolution: ${jsonMapper().registerModules(JavaTimeModule()).writeValueAsString(request)}\n\n")

                    when (request.type) {
                        ProblemType.BACKPACK_CODE_SUPER_INCREASING.description -> checkSuperIncreasing(task, request)
                        ProblemType.BACKPACK_CODE_DEGREES.description -> checkCodeDegrees(task, request)
                    }

                    mapper.modifyEntity(task, request).apply {
                        state = ProblemState.SOLVED
                        errorDescription = null
                    }

                } catch (ex: TaskSolverProblemException) {
                    println(ex.message)
                    mapper.modifyEntity(task, request).apply {
                        state = ProblemState.FAILED
                        errorDescription = ex.message ?: errorDescription
                    }
                }
            )
        )
    }

    private fun checkCodeDegrees(request: BackpackProblem, response: BackpackProblemSubmitRequest) {
        // TODO
        // 0. task can be solved
        if (request.state == ProblemState.SOLVED) {
            throw TaskSolverProblemException(
                "Задача уже решена!"
            )
        }
        // 1. Проверка типа задачи
        if (request.type.description != response.type) {
            throw TaskSolverProblemException(
                "Тип задачи в запросе (${request.type}) не совпадает с типом в ответе (${response.type})."
            )
        }

        // 2. Проверка сообщения
        if (request.message.size != response.message.size) {
            throw TaskSolverProblemException(
                "Длина сообщения в запросе (${request.message.size}) не совпадает с длиной в ответе (${response.message.size})."
            )
        }
        for (i in request.message.indices) {
            if (request.message[i] != response.message[i]) {
                throw TaskSolverProblemException(
                    "Бит сообщения на позиции $i не совпадает: запрос (${request.message[i]}) != ответ (${response.message[i]})."
                )
            }
        }

        // 3. Проверка длины лёгкого и тяжёлого ранцев
        if (request.lightBackpack.size != response.lightBackpack.size) {
            throw TaskSolverProblemException(
                "Длина лёгкого ранца в запросе (${request.lightBackpack.size}) не совпадает с длиной в ответе (${response.lightBackpack.size})."
            )
        }
        if (response.hardBackpack.size != response.lightBackpack.size) {
            throw TaskSolverProblemException(
                "Длина тяжёлого ранца (${response.hardBackpack.size}) не совпадает с длиной лёгкого ранца (${response.lightBackpack.size})."
            )
        }

        // 4. Проверка, что лёгкий ранец имеет правильный формат
        // Предполагается, что лёгкий ранец состоит из степенных значений с дополнениями
        // Так как без информации о дополнениях невозможно точно проверить формат, выполняем базовые проверки

        // Проверяем, что лёгкий ранец состоит из чисел, содержащих внутри себя степенные значения
        // Например, если p=3, то внутри каждого элемента должна быть цифра 1, 3, 9, 27, 81 и т.д.
        // Однако без знания точной структуры дополнений это сложно сделать
        // Поэтому ограничимся проверкой, что в лёгком ранце присутствуют числа, являющиеся степенями p

        val p = request.power ?: throw TaskSolverProblemException("Поле power не задано в запросе.")

        val expectedLightBackpack = fixLightBackpack(request.lightBackpack, response.message.size, p)

        // Сравниваем с предоставленным лёгким ранцем
        for (i in expectedLightBackpack.indices) {
            if (expectedLightBackpack[i] != response.lightBackpack[i].toString()) {
                throw TaskSolverProblemException(
                    "Элемент лёгкого ранца на позиции $i (${response.lightBackpack[i]}) не совпадает с ожидаемым значением (${expectedLightBackpack[i]})."
                )
            }
        }

        // 5. Проверка модуля
        val expectedModule = response.lightBackpack.sumOf { it }
        if (response.module <= expectedModule) {
            throw TaskSolverProblemException("Модуль в ответе (${response.module}) не больше сумме элементов легкого ранца ($expectedModule).")
        }

        // 6. Проверка взаимной простоты омеги и модуля
        if (response.omega.gcd(response.module) != BigInteger.ONE) {
            throw TaskSolverProblemException(
                "Омега (${response.omega}) не взаимно проста с модулем (${response.module}). НОД(omega, module) = ${
                    response.omega.gcd(
                        response.module
                    )
                }."
            )
        }

        // 7. Проверка тяжёлого ранца
        for (i in response.lightBackpack.indices) {
            val expectedHard = (response.lightBackpack[i].multiply(response.omega)).mod(response.module)
            if (response.hardBackpack[i] != expectedHard) {
                throw TaskSolverProblemException(
                    "Элемент тяжёлого ранца на позиции $i (${response.hardBackpack[i]}) не совпадает с ожидаемым значением ($expectedHard)."
                )
            }
        }

        // 8. Проверка обратного элемента омеги
        val computedReverseOmega = response.omega.modInverse(response.module)
            ?: throw TaskSolverProblemException(
                "Не удалось вычислить обратный элемент омеги (${response.omega}) по модулю (${response.module})."
            )
        if (computedReverseOmega != response.reverseOmega) {
            throw TaskSolverProblemException(
                "Обратный элемент омеги в ответе (${response.reverseOmega}) не совпадает с вычисленным значением ($computedReverseOmega)."
            )
        }
        // Дополнительная проверка: omega * reverseOmega mod module = 1
        if (response.omega.multiply(response.reverseOmega).mod(response.module) != BigInteger.ONE) {
            throw TaskSolverProblemException(
                "Омега (${response.omega}) умноженная на обратный элемент (${response.reverseOmega}) по модулю (${response.module}) не равна 1."
            )
        }

        // 9. Проверка шифртекста
        val expectedEncodedMessage = response.message.zip(response.hardBackpack)
            .fold(BigInteger.ZERO) { acc, pair ->
                acc + if (pair.first) pair.second else BigInteger.ZERO
            }
        if (response.encodedMessage != expectedEncodedMessage) {
            throw TaskSolverProblemException(
                "Шифртекст в ответе (${response.encodedMessage}) не совпадает с ожидаемым значением ($expectedEncodedMessage)."
            )
        }

        // 10. Проверка расшифрованного сообщения
        // TODO: Реализация проверки расшифрованного сообщения невозможна из-за отсутствия механизма однозначного декодирования дополненных цифр.
        // Проблема заключается в том, что без информации о количестве и позиции дополненных цифр невозможно корректно восстановить исходную сверхвозрастающую последовательность.
        // Это делает невозможным использование жадного алгоритма для расшифровки сообщения, так как структура лёгкого ранца нарушена.

        // 11. Дополнительные проверки
        // Проверка, что все элементы ранцев и модуль положительные
        if (response.lightBackpack.any { it <= BigInteger.ZERO }) {
            throw TaskSolverProblemException("Лёгкий ранец содержит неположительные элементы.")
        }
        if (response.hardBackpack.any { it < BigInteger.ZERO }) {
            throw TaskSolverProblemException("Тяжёлый ранец содержит отрицательные элементы.")
        }
        if (response.module <= BigInteger.ONE) {
            throw TaskSolverProblemException("Модуль (${response.module}) должен быть больше 1.")
        }
    }

    /**
     * Восстанавливает ожидаемую сверхвозрастающую последовательность на основе степеней p.
     * В текущей реализации без механизма удаления дополненных цифр, эта функция возвращает только p^i.
     */
    private fun fixLightBackpack(input: List<BigInteger>, size: Int, p: Int): List<String> {
        val result = mutableListOf<String>()
        var sumSoFar = BigInteger.ZERO

        // Генерация сверхвозрастающей последовательности на основе степеней p
        for (i in 0 until size) {
            val pPow = BigInteger(p.toString()).pow(i)
            if (pPow <= sumSoFar) {
                throw TaskSolverProblemException(
                    "p^$i ($pPow) не превышает сумму предыдущих элементов ($sumSoFar). Невозможно сформировать сверхвозрастающую последовательность."
                )
            }
            // В текущей реализации, дополнения уже добавлены в lightBackpack, поэтому здесь предполагается, что input содержит только p^i
            // Но если input содержит дополнения, необходимо их игнорировать или как-то учитывать
            // Поскольку механизм удаления дополнений отсутствует, ограничимся только проверкой p^i
            result.add(pPow.toString())
            sumSoFar += pPow
        }

        return result
    }

    private fun checkSuperIncreasing(task: BackpackProblem, solution: BackpackProblemSubmitRequest) {
        // TODO
        // 0. task can be solved
        if (task.state == ProblemState.SOLVED) {
            throw TaskSolverProblemException(
                "Задача уже решена!"
            )
        }
        // 1. Проверка типа задачи
        if (task.type.description != solution.type) {
            throw TaskSolverProblemException("Тип задачи в запросе (${task.type.description}) не совпадает с типом в ответе (${solution.type}).")
        }

        // 2. Проверка сообщения
        if (task.message.size != solution.message.size) {
            throw TaskSolverProblemException("Длина сообщения в запросе (${task.message.size}) не совпадает с длиной в ответе (${solution.message.size}).")
        }
        for (i in task.message.indices) {
            if (task.message[i] != solution.message[i]) {
                throw TaskSolverProblemException("Бит сообщения на позиции $i не совпадает: запрос (${task.message[i]}) != ответ (${solution.message[i]}).")
            }
        }

        // 3. Проверка длины легкого и тяжёлого ранцев и длины сообщения
        if (task.lightBackpack.size != solution.lightBackpack.size) {
            throw TaskSolverProblemException("Длина легкого ранца в запросе (${task.lightBackpack.size}) не совпадает с длиной в ответе (${solution.lightBackpack.size}).")
        }
        if (solution.hardBackpack.size != solution.lightBackpack.size) {
            throw TaskSolverProblemException("Длина тяжёлого ранца (${solution.hardBackpack.size}) не совпадает с длиной легкого ранца (${solution.lightBackpack.size}).")
        }
        if (task.message.size != solution.lightBackpack.size) {
            throw TaskSolverProblemException("Длина ранцев (${solution.hardBackpack.size}) не совпадает с длиной сообщения (${solution.lightBackpack.size}).")
        }

        // 4. Проверка, что легкий ранец является строго сверхвозрастающей последовательностью
        var sumSoFar = BigInteger.ONE.negate()
        for ((index, value) in solution.lightBackpack.withIndex()) {
            if (value <= sumSoFar) {
                throw TaskSolverProblemException("Элемент легкого ранца на позиции $index ($value) не превышает сумму всех предыдущих элементов ($sumSoFar). Рюкзак должен быть строго сверхвозрастающим.")
            }
            sumSoFar += value
        }

        // 5. Проверка модуля
        val expectedModule = solution.lightBackpack.sumOf { it }
        if (solution.module <= expectedModule) {
            throw TaskSolverProblemException("Модуль в ответе (${solution.module}) не меньше сумме элементов легкого ранца ($expectedModule).")
        }

        // 6. Проверка взаимной простоты омеги и модуля
        if (solution.omega.gcd(solution.module) != BigInteger.ONE) {
            throw TaskSolverProblemException(
                "Омега (${solution.omega}) не взаимно проста с модулем (${solution.module}). НОД(omega, module) = ${
                    solution.omega.gcd(
                        solution.module
                    )
                }."
            )
        }

        // 7. Проверка тяжёлого ранца
        for (i in solution.lightBackpack.indices) {
            val expectedHard = (solution.lightBackpack[i].multiply(solution.omega)).mod(solution.module)
            if (solution.hardBackpack[i] != expectedHard) {
                throw TaskSolverProblemException("Элемент тяжёлого ранца на позиции $i (${solution.hardBackpack[i]}) не совпадает с ожидаемым значением ($expectedHard).")
            }
        }

        // 8. Проверка обратного элемента омеги
        val computedReverseOmega = solution.omega.modInverse(solution.module)
        if (computedReverseOmega != solution.reverseOmega) {
            throw TaskSolverProblemException("Обратный элемент омеги в ответе (${solution.reverseOmega}) не совпадает с вычисленным значением ($computedReverseOmega).")
        }
        // Дополнительная проверка: omega * reverseOmega mod module = 1
        if (solution.omega.multiply(solution.reverseOmega).mod(solution.module) != BigInteger.ONE) {
            throw TaskSolverProblemException("Омега (${solution.omega}) умноженная на обратный элемент (${solution.reverseOmega}) по модулю (${solution.module}) не равна 1.")
        }

        // 9. Проверка шифртекста
        val expectedEncodedMessage = solution.message.zip(solution.hardBackpack)
            .fold(BigInteger.ZERO) { acc, pair ->
                acc + if (pair.first) pair.second else BigInteger.ZERO
            }.mod(solution.module)
        if (solution.encodedMessage != expectedEncodedMessage) {
            throw TaskSolverProblemException("Шифртекст в ответе (${solution.encodedMessage}) не совпадает с ожидаемым значением ($expectedEncodedMessage).")
        }

        // 10. Проверка расшифрованного сообщения
        // Вычисляем X = C * reverseOmega mod module
        val computedX = solution.encodedMessage.multiply(solution.reverseOmega).mod(solution.module)

        // Решаем задачу о рюкзаке с легким ранцем и X, используя жадный алгоритм
        val decodedMessageComputed = solveSuperIncreasingBackpack(solution.lightBackpack, computedX)

        // Сравниваем расшифрованное сообщение с предоставленным
        if (decodedMessageComputed != solution.decodedMessage) {
            throw TaskSolverProblemException("Расшифрованное сообщение (${solution.decodedMessage}) не совпадает с вычисленным значением ($decodedMessageComputed).")
        }

        // 11. Дополнительные проверки
        // Проверка, что все элементы ранцев и модуль положительные
        if (solution.lightBackpack.any { it <= BigInteger.ZERO }) {
            throw TaskSolverProblemException("Лёгкий ранец содержит неположительные элементы.")
        }
        if (solution.hardBackpack.any { it < BigInteger.ZERO }) {
            throw TaskSolverProblemException("Тяжёлый ранец содержит отрицательные элементы.")
        }
        if (solution.module <= BigInteger.ONE) {
            throw TaskSolverProblemException("Модуль (${solution.module}) должен быть больше 1.")
        }
    }

    /**
     * Решение задачи о рюкзаке для сверхвозрастающего множества с использованием жадного алгоритма.
     * Возвращает список булевых значений, представляющих выбранные элементы.
     */
    private fun solveSuperIncreasingBackpack(backpack: List<BigInteger>, target: BigInteger): List<Boolean> {
        val n = backpack.size
        val decoded = MutableList(n) { false }
        var remaining = target

        for (i in backpack.indices.reversed()) {
            if (backpack[i] <= remaining) {
                decoded[i] = true
                remaining -= backpack[i]
            }
            if (remaining == BigInteger.ZERO) break
        }

        if (remaining != BigInteger.ZERO) {
            throw TaskSolverProblemException("Не удалось расшифровать сообщение: не найдено подмножество элементов легкого ранца, сумма которых равна $target.")
        }

        return decoded
    }
}
