package ru.command.mephi12.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemSubmitRequest
import ru.command.mephi12.dto.ProblemType
import ru.command.mephi12.service.BackpackProblemSolverService
import ru.command.mephi12.service.ProblemsCheckerService
import java.util.*

// TODO replace logging with log interceptor
@RestController
@RequestMapping("/tasks/backpack")
class BackpackProblemController(
    val service: BackpackProblemSolverService,
    val problemsCheckerService: ProblemsCheckerService
) {
    companion object {
        val log = LoggerFactory.getLogger(BackpackProblemController::class.java)
        val objectMapper = ObjectMapper().registerModules(JavaTimeModule())
    }
    @GetMapping("/editorial")
    fun editorial(@RequestParam("type") type: ProblemType) = service.solve(BackpackProblemEditorialRequest(type = type))
        .also {
            log.info("GET /tasks/backpack/editorial. Response: {}", objectMapper.writeValueAsString(it))
        }

    @PostMapping("/editorial")
    fun editorial(@RequestBody request: BackpackProblemEditorialRequest) = service.solve(request)
        .also {
            log.info("POST /tasks/backpack/editorial. Response: {}", objectMapper.writeValueAsString(it))
        }

    @GetMapping
    fun getTask() = problemsCheckerService.generateTask().also {
        log.info("GET /tasks/backpack. Response: {}", objectMapper.writeValueAsString(it))
    }
    @PutMapping
    fun submitTask(@RequestParam("id") id: UUID, @RequestBody request: BackpackProblemSubmitRequest) = problemsCheckerService.check(id, request).also {
        log.info("PUT /tasks/backpack. Response: {}", objectMapper.writeValueAsString(it))

    }
}