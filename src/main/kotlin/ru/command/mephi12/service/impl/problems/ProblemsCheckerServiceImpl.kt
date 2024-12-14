package ru.command.mephi12.service.impl.problems

import org.springframework.stereotype.Component
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemSubmitRequest
import ru.command.mephi12.dto.BackpackProblemType
import ru.command.mephi12.exception.TaskSolverProblemException
import ru.command.mephi12.service.ProblemsCheckerService
import java.math.BigInteger

@Component
class ProblemsCheckerServiceImpl : ProblemsCheckerService {
    override fun check(request: BackpackProblemEditorialRequest, response: BackpackProblemSubmitRequest) {
        when (request.type) {
            BackpackProblemType.CODE_SUPER_INCREASING -> checkSuperIncreasing(request, response)
            BackpackProblemType.CODE_DEGREES -> checkCodeDegrees(request, response)
        }
    }

    private fun checkCodeDegrees(request: BackpackProblemEditorialRequest, response: BackpackProblemSubmitRequest) {
        // 1. Проверка типа задачи
        if (request.type != response.type) {
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
        // TODO: Рюкзак в задаче может быть задан частично. Сейчас прверка происходит исходя из того, что лёгкий рюкзак задан ПОЛНОСТЬЮ
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
        if (response.module > expectedModule) {
            throw TaskSolverProblemException("Модуль в ответе (${response.module}) не меньше сумме элементов легкого ранца ($expectedModule).")
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

    private fun checkSuperIncreasing(request: BackpackProblemEditorialRequest, response: BackpackProblemSubmitRequest) {
        // 1. Проверка типа задачи
        if (request.type != response.type) {
            throw TaskSolverProblemException("Тип задачи в запросе (${request.type.text}) не совпадает с типом в ответе (${response.type.text}).")
        }

        // 2. Проверка сообщения
        if (request.message.size != response.message.size) {
            throw TaskSolverProblemException("Длина сообщения в запросе (${request.message.size}) не совпадает с длиной в ответе (${response.message.size}).")
        }
        for (i in request.message.indices) {
            if (request.message[i] != response.message[i]) {
                throw TaskSolverProblemException("Бит сообщения на позиции $i не совпадает: запрос (${request.message[i]}) != ответ (${response.message[i]}).")
            }
        }

        // 3. Проверка длины легкого и тяжёлого ранцев и длины сообщения
        // TODO: Рюкзак в задаче может быть задан частично. Сейчас прверка происходит исходя из того, что лёгкий рюкзак задан ПОЛНОСТЬЮ
        if (request.lightBackpack.size != response.lightBackpack.size) {
            throw TaskSolverProblemException("Длина легкого ранца в запросе (${request.lightBackpack.size}) не совпадает с длиной в ответе (${response.lightBackpack.size}).")
        }
        if (response.hardBackpack.size != response.lightBackpack.size) {
            throw TaskSolverProblemException("Длина тяжёлого ранца (${response.hardBackpack.size}) не совпадает с длиной легкого ранца (${response.lightBackpack.size}).")
        }
        if (request.message.size != response.lightBackpack.size) {
            throw TaskSolverProblemException("Длина ранцев (${response.hardBackpack.size}) не совпадает с длиной сообщения (${response.lightBackpack.size}).")
        }

        // 4. Проверка, что легкий ранец является строго сверхвозрастающей последовательностью
        var sumSoFar = BigInteger.ZERO
        for ((index, value) in response.lightBackpack.withIndex()) {
            if (value <= sumSoFar) {
                throw TaskSolverProblemException("Элемент легкого ранца на позиции $index ($value) не превышает сумму всех предыдущих элементов ($sumSoFar). Рюкзак должен быть строго сверхвозрастающим.")
            }
            sumSoFar += value
        }

        // 5. Проверка модуля
        val expectedModule = response.lightBackpack.sumOf { it }
        if (response.module > expectedModule) {
            throw TaskSolverProblemException("Модуль в ответе (${response.module}) не меньше сумме элементов легкого ранца ($expectedModule).")
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
                throw TaskSolverProblemException("Элемент тяжёлого ранца на позиции $i (${response.hardBackpack[i]}) не совпадает с ожидаемым значением ($expectedHard).")
            }
        }

        // 8. Проверка обратного элемента омеги
        val computedReverseOmega = response.omega.modInverse(response.module)
        if (computedReverseOmega != response.reverseOmega) {
            throw TaskSolverProblemException("Обратный элемент омеги в ответе (${response.reverseOmega}) не совпадает с вычисленным значением ($computedReverseOmega).")
        }
        // Дополнительная проверка: omega * reverseOmega mod module = 1
        if (response.omega.multiply(response.reverseOmega).mod(response.module) != BigInteger.ONE) {
            throw TaskSolverProblemException("Омега (${response.omega}) умноженная на обратный элемент (${response.reverseOmega}) по модулю (${response.module}) не равна 1.")
        }

        // 9. Проверка шифртекста
        val expectedEncodedMessage = response.message.zip(response.hardBackpack)
            .fold(BigInteger.ZERO) { acc, pair ->
                acc + if (pair.first) pair.second else BigInteger.ZERO
            }
        if (response.encodedMessage != expectedEncodedMessage) {
            throw TaskSolverProblemException("Шифртекст в ответе (${response.encodedMessage}) не совпадает с ожидаемым значением ($expectedEncodedMessage).")
        }

        // 10. Проверка расшифрованного сообщения
        // Вычисляем X = C * reverseOmega mod module
        val computedX = response.encodedMessage.multiply(response.reverseOmega).mod(response.module)

        // Решаем задачу о рюкзаке с легким ранцем и X, используя жадный алгоритм
        val decodedMessageComputed = solveSuperIncreasingBackpack(response.lightBackpack, computedX)

        // Сравниваем расшифрованное сообщение с предоставленным
        if (decodedMessageComputed != response.decodedMessage) {
            throw TaskSolverProblemException("Расшифрованное сообщение (${response.decodedMessage}) не совпадает с вычисленным значением ($decodedMessageComputed).")
        }

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
