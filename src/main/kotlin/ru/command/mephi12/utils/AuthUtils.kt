package ru.command.mephi12.utils

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

fun getPrincipal(): UUID = (SecurityContextHolder.getContext().authentication.principal as UUID)

fun getAuthorities(): MutableCollection<out GrantedAuthority> = (SecurityContextHolder.getContext().authentication.authorities!!)
