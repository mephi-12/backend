package ru.command.mephi12.database.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_group_problem_configuration")
class UserGroupProblemConfiguration(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    val group: UserGroup,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_configuration_id")
    val problemConfiguration: ProblemConfiguration,

    var enabled: Boolean = true
) : AbstractEntity()
