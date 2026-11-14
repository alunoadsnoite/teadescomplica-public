package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.ExperienceStrategyCrossRefEntity
import br.com.teadescomplica.domain.model.ExperienceStrategyCrossRef

object ExperienceStrategyCrossRefMapper {
    fun toDomain(entity: ExperienceStrategyCrossRefEntity): ExperienceStrategyCrossRef = entity.toDomain()

    fun toEntity(crossRef: ExperienceStrategyCrossRef): ExperienceStrategyCrossRefEntity =
        ExperienceStrategyCrossRefEntity.fromDomain(crossRef)
}
