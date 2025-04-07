package ru.command.mephi12.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.command.mephi12.constants.API_PUBLIC
import ru.command.mephi12.dto.auth.LoginRequest
import ru.command.mephi12.dto.auth.LoginResponse
import ru.command.mephi12.dto.auth.RegistrationRequest
import ru.command.mephi12.service.AuthService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val service: AuthService,
) {

    @PostMapping("/registration")
    fun register(
        @Valid @RequestBody
        request: RegistrationRequest,
    ): LoginResponse = service.registration(request)


    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse = service.login(request)
}
