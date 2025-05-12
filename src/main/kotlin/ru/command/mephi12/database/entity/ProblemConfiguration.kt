package ru.command.mephi12.database.entity

import jakarta.persistence.*

@Entity
@Table(name = "problem_configuration")
class ProblemConfiguration(
    val name: String
) : AbstractEntity() {

    @ElementCollection
    @CollectionTable(
        name = "problem_configuration_task_services",
        joinColumns = [JoinColumn(name = "problem_configuration_id")]
    )
    @Column(name = "task_service")
    var taskServices: MutableList<String> = mutableListOf()

    @OneToMany(
        mappedBy = "problemConfiguration",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var groupLinks: MutableSet<UserGroupProblemConfiguration> = mutableSetOf()
}
