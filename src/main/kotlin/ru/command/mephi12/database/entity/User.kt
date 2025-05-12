package ru.command.mephi12.database.entity

import jakarta.persistence.*

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    var group: UserGroup? = null

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    var problemSessions: MutableList<ProblemSession> = mutableListOf()
}
