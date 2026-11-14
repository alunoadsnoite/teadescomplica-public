package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.ScenarioPreparation

@Entity(
    tableName = "scenario_preparations",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scenario_id"), Index("updated_at")]
)
data class ScenarioPreparationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "scheduled_at") val scheduledAt: Long?,
    @ColumnInfo(name = "what_to_expect") val whatToExpect: String,
    @ColumnInfo(name = "what_to_bring") val whatToBring: String,
    @ColumnInfo(name = "important_information") val importantInformation: String,
    @ColumnInfo(name = "personal_notes") val personalNotes: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): ScenarioPreparation =
        ScenarioPreparation(
            scenarioId = scenarioId,
            location = location,
            scheduledAt = scheduledAt,
            whatToExpect = whatToExpect,
            whatToBring = whatToBring,
            importantInformation = importantInformation,
            personalNotes = personalNotes,
            updatedAt = updatedAt
        )

    companion object {
        fun fromDomain(preparation: ScenarioPreparation): ScenarioPreparationEntity =
            ScenarioPreparationEntity(
                id = 0,
                scenarioId = preparation.scenarioId,
                location = preparation.location,
                scheduledAt = preparation.scheduledAt,
                whatToExpect = preparation.whatToExpect,
                whatToBring = preparation.whatToBring,
                importantInformation = preparation.importantInformation,
                personalNotes = preparation.personalNotes,
                updatedAt = preparation.updatedAt
            )
    }
}
