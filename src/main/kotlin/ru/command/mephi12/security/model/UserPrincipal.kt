package ru.command.mephi12.security.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class UserPrincipal(
    val userId: Long,
    val authorities: List<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials() = null

    override fun getPrincipal() = userId
}
