package ru.command.mephi12.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.command.mephi12.service.ElGamalService

// TODO replace logging with log interceptor
@RestController
@RequestMapping("/tasks/eigamal")
class EIGamalController(
    private val service: ElGamalService,
    private val objectMapper: ObjectMapper
) {
    companion object {
        val log = LoggerFactory.getLogger(EIGamalController::class.java)
    }

    @GetMapping("/editorial")
    fun editorial() = service.generateDemo().also {
        log.info("GET /tasks/eigamal/editorial. Response: {}", objectMapper.writeValueAsString(it))
    }

    @GetMapping
    fun getTask() = service.generateTask().also {
        log.info("GET /tasks/eigamal. Response: {}", objectMapper.writeValueAsString(it))
    }

//    @PutMapping
//    fun submitTask(@RequestParam("id") id: UUID, @RequestBody request: BackpackProblemSubmitRequest) =
//        service.checkSolution(id, request).also {
//            log.info("PUT /tasks/backpack. Response: {}", objectMapper.writeValueAsString(it))
//        }
}