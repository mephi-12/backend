package ru.command.mephi12.database.entity

import jakarta.persistence.*
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.dto.modern_problem.ProblemSessionDto


@Entity
@Table(
    name = "problem_session",
)
class ProblemSession(
    @Column(name = "session_state")
    @Enumerated(EnumType.STRING)
    var sessionState: ProblemState = ProblemState.NEW,
) : AbstractEntity() {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @OneToMany(mappedBy = "problemSession", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var problems: MutableList<Problem> = mutableListOf()

    fun toDto() : ProblemSessionDto =
        ProblemSessionDto(
            id = id,
            createdAt = createdAt,
            state = sessionState,
            userId = user.id,
            problems = problems.map { it.toDto() },
        )
}
