package ru.command.mephi12.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.command.mephi12.service.impl.problems.ProblemDecorator

@RestController
@RequestMapping("/moderator")
class ModeratorController(
    private val problemDecorator: ProblemDecorator
) {
    @GetMapping("/tasks/session")
    fun getSessions() = problemDecorator.getSessions()
}