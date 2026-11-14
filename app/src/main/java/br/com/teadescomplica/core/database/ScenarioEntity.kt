package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.Scenario

@Entity(tableName = "scenarios")
data class ScenarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "objective") val objective: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): Scenario =
        Scenario(
            id = id,
            title = title,
            description = description,
            objective = objective,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {
        fun fromDomain(scenario: Scenario): ScenarioEntity =
            ScenarioEntity(
                id = scenario.id,
                title = scenario.title,
                description = scenario.description,
                objective = scenario.objective,
                createdAt = scenario.createdAt,
                updatedAt = scenario.updatedAt
            )
    }
}
