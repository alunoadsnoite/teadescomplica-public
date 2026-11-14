package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.PossibleSituation
import br.com.teadescomplica.domain.model.SituationLikelihood

@Entity(
    tableName = "possible_situations",
    foreignKeys =
        [
            ForeignKey(
                entity = ScenarioEntity::class,
                parentColumns = ["id"],
                childColumns = ["scenario_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("scenario_id"), Index("created_at")],
)
data class PossibleSituationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "likelihood") val likelihood: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
) {
    fun toDomain(): PossibleSituation =
        PossibleSituation(
            id = id,
            scenarioId = scenarioId,
            description = description,
            likelihood = likelihood?.let { SituationLikelihood.valueOf(it) },
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(situation: PossibleSituation): PossibleSituationEntity =
            PossibleSituationEntity(
                id = situation.id,
                scenarioId = situation.scenarioId,
                description = situation.description,
                likelihood = situation.likelihood?.name,
                createdAt = situation.createdAt,
            )
    }
}
