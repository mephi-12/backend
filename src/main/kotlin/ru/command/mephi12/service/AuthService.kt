package ru.command.mephi12.service

import ru.command.mephi12.dto.auth.LoginRequest
import ru.command.mephi12.dto.auth.LoginResponse
import ru.command.mephi12.dto.auth.RegistrationRequest

interface AuthService {
    fun registration(request: RegistrationRequest) : LoginResponse

    fun login(request: LoginRequest): LoginResponse
}