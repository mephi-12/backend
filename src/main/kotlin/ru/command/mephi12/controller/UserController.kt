package ru.command.mephi12.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.command.mephi12.service.HistoryService

@RestController
@RequestMapping("/users")
class UserController(
    private val historyService: HistoryService,
) {
    @GetMapping("/me/tasks")
    fun getMyTasks() = historyService.findAll()
}