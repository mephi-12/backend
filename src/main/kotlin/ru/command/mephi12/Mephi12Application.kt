package ru.command.mephi12

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Mephi12Application

fun main(args: Array<String>) {
//    Нужно для подгрузки переменных окружения если потребуется запустить не через контейнер
    loadEnvParams()

    runApplication<Mephi12Application>(*args)
}

private fun loadEnvParams() {
    try {
        val dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load()

        dotenv.entries().forEach { envParam ->
            System.setProperty(envParam.key, envParam.value)
        }
    } catch (e: Exception) {
        println("Warning: Could not load .env file. Using system environment variables instead.")
    }
}