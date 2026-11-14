package br.com.teadescomplica.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.teadescomplica.domain.model.PersonalStrategy

@Entity(
    tableName = "personal_strategies",
    indices = [
        Index("title"),
        Index("created_at")
    ]
)
data class PersonalStrategyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    fun toDomain(): PersonalStrategy =
        PersonalStrategy(
            id = id,
            title = title,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    companion object {
        fun fromDomain(strategy: PersonalStrategy): PersonalStrategyEntity =
            PersonalStrategyEntity(
                id = strategy.id,
                title = strategy.title,
                description = strategy.description,
                createdAt = strategy.createdAt,
                updatedAt = strategy.updatedAt
            )
    }
}
