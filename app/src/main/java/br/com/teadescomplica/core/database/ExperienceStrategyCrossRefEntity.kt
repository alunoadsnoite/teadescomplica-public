package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.ExperienceStrategyCrossRef

@Entity(
    tableName = "experience_strategy_cross_ref",
    primaryKeys = ["experience_id", "strategy_id"],
    foreignKeys = [
        ForeignKey(
            entity = PersonalExperienceEntity::class,
            parentColumns = ["id"],
            childColumns = ["experience_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonalStrategyEntity::class,
            parentColumns = ["id"],
            childColumns = ["strategy_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("experience_id"),
        Index("strategy_id")
    ]
)
data class ExperienceStrategyCrossRefEntity(
    @ColumnInfo(name = "experience_id") val experienceId: Long,
    @ColumnInfo(name = "strategy_id") val strategyId: Long
) {
    fun toDomain(): ExperienceStrategyCrossRef =
        ExperienceStrategyCrossRef(
            experienceId = experienceId,
            strategyId = strategyId
        )

    companion object {
        fun fromDomain(crossRef: ExperienceStrategyCrossRef): ExperienceStrategyCrossRefEntity =
            ExperienceStrategyCrossRefEntity(
                experienceId = crossRef.experienceId,
                strategyId = crossRef.strategyId
            )
    }
}
