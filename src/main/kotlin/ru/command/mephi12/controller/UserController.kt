package ru.command.mephi12.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.command.mephi12.service.HistoryService
import ru.command.mephi12.service.UserGroupService
import ru.command.mephi12.service.UserService

@RestController
@RequestMapping("/users")
class UserController(
    private val historyService: HistoryService,
    private val userService: UserService,
    private val userGroupService: UserGroupService,
) {
    @GetMapping("/me/tasks")
    fun getMyTasks() = historyService.findAll()
    @PutMapping("/me/groups")
    fun updateMyGroup(@RequestParam("group") group: String) = userService.setMyGroup(group)
    @GetMapping("/groups")
    fun getGroups() = userGroupService.findAll()

}