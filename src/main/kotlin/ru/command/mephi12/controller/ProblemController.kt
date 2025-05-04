package ru.command.mephi12.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.command.mephi12.database.entity.ProblemSession
import ru.command.mephi12.dto.ProblemSubmitResponse
import ru.command.mephi12.dto.modern_problem.ProblemSessionDto
import ru.command.mephi12.service.impl.problems.ProblemDecorator
import java.util.*

@RestController
@RequestMapping("/tasks")
class ProblemController(
    private val problemDecorator: ProblemDecorator
) {
    companion object {
        val log = LoggerFactory.getLogger(ProblemController::class.java)
    }
    @PostMapping("/session")
    fun createProblemSession(@RequestParam("configuration") config: String) : ProblemSessionDto =
        log.info("Получен запрос на формирование набора задач с типом $config").run {
            problemDecorator.createSolvingSession(config)
        }

    @PutMapping("/submit")
    fun check(@RequestParam("statementId") id: UUID, @RequestBody body: String) : ProblemSubmitResponse =
        log.info("Получен запрос на проверку задачи с id $id и телом $body").run {
            problemDecorator.check(id, body)
        }
}