package ru.command.mephi12.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.command.mephi12.dto.BackpackProblemRequest
import ru.command.mephi12.dto.BackpackProblemType
import ru.command.mephi12.service.BackpackProblemSolverService
import ru.command.mephi12.service.problems.backpack.BackpackProblemHandler

@RestController
@RequestMapping("/tasks/backpack")
class BackpackProblemController(
    val service: BackpackProblemSolverService,
) {
    @GetMapping("/editorial")
    fun editorial(@RequestParam("type") type: BackpackProblemType) = service.solve(BackpackProblemRequest(type = type))

}