package ru.command.mephi12.database.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "user_group")
class UserGroup(
    val name: String
) : AbstractEntity() {
    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL])
    var users: MutableList<User> = mutableListOf()
    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    var problemConfigs: MutableSet<UserGroupProblemConfiguration> = mutableSetOf()

}