package ru.command.mephi12.database.entity

import jakarta.persistence.*
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType


@Entity
@Table(
    name = "problem_history",
)
class ProblemHistory(
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ProblemType,
    @Column(name = "statement", nullable = false)
    var statement: String,
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    var state: ProblemState = ProblemState.NEW
) : AbstractEntity() {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private val user: User? = null
}
