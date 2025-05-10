package ru.command.mephi12.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.command.mephi12.dto.backdoor.BackpackBackdoorProblemEditorialRequest
import ru.command.mephi12.service.ElGamalService
import ru.command.mephi12.service.impl.problems.backpack.BackpackBackDoorDegreesProblemSolverServiceImpl

// TODO replace logging with log interceptor
@RestController
@RequestMapping("/backdoor")
class BackdoorController(
    private val service: BackpackBackDoorDegreesProblemSolverServiceImpl,
    private val objectMapper: ObjectMapper
) {
    companion object {
        val log = LoggerFactory.getLogger(BackdoorController::class.java)
    }

    @GetMapping("/editorial")
    fun editorial(@RequestBody request: BackpackBackdoorProblemEditorialRequest) = service.getEditorialTask(
        request
    ).also {
        log.info("GET /tasks/eigamal/editorial. Response: {}", objectMapper.writeValueAsString(it))
    }
}