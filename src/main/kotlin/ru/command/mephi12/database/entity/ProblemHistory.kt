package ru.command.mephi12.database.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
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
    @Column(name = "problem")
) : AbstractEntity() {

}
