package ru.command.mephi12.database.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`User`")
class User(
    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "name", nullable = false)
    var name: String,

    ) : AbstractEntity() {
    @Column(name = "hash", nullable = false)
    var hash: String? = null

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    var problemSessions: MutableList<ProblemSession> = mutableListOf()
}
