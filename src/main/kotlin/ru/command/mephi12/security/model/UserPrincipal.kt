package ru.command.mephi12.security.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.util.*

class UserPrincipal(
    val userId: UUID,
    val authorities: List<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials() = null

    override fun getPrincipal() = userId
}
