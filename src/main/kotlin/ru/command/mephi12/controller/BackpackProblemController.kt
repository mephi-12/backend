package ru.command.mephi12.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemResponse
import ru.command.mephi12.dto.BackpackProblemSubmitRequest
import ru.command.mephi12.service.BackpackProblemSolverService
import ru.command.mephi12.service.ProblemsCheckerService
import java.util.*

// TODO replace logging with log interceptor
// Deprecated
@RestController
@RequestMapping("/tasks/backpack")
class BackpackProblemController(
    val service: BackpackProblemSolverService,
//    val problemsCheckerService: ProblemsCheckerService<BackpackProblemResponse, BackpackProblemSubmitRequest>
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

}