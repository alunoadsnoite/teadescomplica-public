package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.AlternativePlan

@Entity(
    tableName = "alternative_plans",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenario_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PossibleSituationEntity::class,
            parentColumns = ["id"],
            childColumns = ["possible_situation_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("scenario_id"),
        Index("possible_situation_id"),
        Index("position")
    ]
)
data class AlternativePlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long,
    @ColumnInfo(name = "possible_situation_id") val possibleSituationId: Long?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): AlternativePlan =
        AlternativePlan(
            id = id,
            scenarioId = scenarioId,
            possibleSituationId = possibleSituationId,
            title = title,
            description = description,
            position = position,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {
        fun fromDomain(plan: AlternativePlan): AlternativePlanEntity =
            AlternativePlanEntity(
                id = plan.id,
                scenarioId = plan.scenarioId,
                possibleSituationId = plan.possibleSituationId,
                title = plan.title,
                description = plan.description,
                position = plan.position,
                createdAt = plan.createdAt,
                updatedAt = plan.updatedAt
            )
    }
}
