package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.PersonalExperience

@Entity(
    tableName = "personal_experiences",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenario_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("scenario_id"),
        Index("occurred_at"),
        Index("created_at"),
        Index("title")
    ]
)
data class PersonalExperienceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "occurred_at") val occurredAt: Long?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "what_helped") val whatHelped: String,
    @ColumnInfo(name = "what_was_difficult") val whatWasDifficult: String,
    @ColumnInfo(name = "next_time_notes") val nextTimeNotes: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): PersonalExperience =
        PersonalExperience(
            id = id,
            scenarioId = scenarioId,
            title = title,
            occurredAt = occurredAt,
            description = description,
            whatHelped = whatHelped,
            whatWasDifficult = whatWasDifficult,
            nextTimeNotes = nextTimeNotes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {
        fun fromDomain(experience: PersonalExperience): PersonalExperienceEntity =
            PersonalExperienceEntity(
                id = experience.id,
                scenarioId = experience.scenarioId,
                title = experience.title,
                occurredAt = experience.occurredAt,
                description = experience.description,
                whatHelped = experience.whatHelped,
                whatWasDifficult = experience.whatWasDifficult,
                nextTimeNotes = experience.nextTimeNotes,
                createdAt = experience.createdAt,
                updatedAt = experience.updatedAt
            )
    }
}
