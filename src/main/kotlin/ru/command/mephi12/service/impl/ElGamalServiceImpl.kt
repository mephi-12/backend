package ru.command.mephi12.service.impl

import org.springframework.stereotype.Service
import ru.command.mephi12.service.ElGamalService
import ru.command.mephi12.utils.mathLibrary.*
import java.math.BigInteger

@Service
class ElGamalServiceImpl : ElGamalService {
    
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
}