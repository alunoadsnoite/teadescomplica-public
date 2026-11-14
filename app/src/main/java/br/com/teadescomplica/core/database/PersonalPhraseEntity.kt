package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.PersonalPhrase

@Entity(
    tableName = "personal_phrases",
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scenario_id"), Index("position")]
)
data class PersonalPhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    fun toDomain(): PersonalPhrase =
        PersonalPhrase(
            id = id,
            scenarioId = scenarioId,
            text = text,
            position = position,
            createdAt = createdAt
        )

    companion object {
        fun fromDomain(phrase: PersonalPhrase): PersonalPhraseEntity =
            PersonalPhraseEntity(
                id = phrase.id,
                scenarioId = phrase.scenarioId,
                text = phrase.text,
                position = phrase.position,
                createdAt = phrase.createdAt
            )
    }
}
