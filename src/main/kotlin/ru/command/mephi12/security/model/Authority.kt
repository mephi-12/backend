package ru.command.mephi12.security.model

import org.springframework.security.core.GrantedAuthority
import ru.command.mephi12.constants.ADMIN_ROLE
import ru.command.mephi12.constants.USER_ROLE

enum class Authority(val authority: GrantedAuthority) {
    USER(GrantedAuthority { USER_ROLE }), ADMIN(GrantedAuthority { ADMIN_ROLE })
}
