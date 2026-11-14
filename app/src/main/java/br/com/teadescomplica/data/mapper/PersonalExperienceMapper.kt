package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.PersonalExperienceEntity
import br.com.teadescomplica.domain.model.PersonalExperience

object PersonalExperienceMapper {
    fun toDomain(entity: PersonalExperienceEntity): PersonalExperience = entity.toDomain()

    fun toEntity(experience: PersonalExperience): PersonalExperienceEntity =
        PersonalExperienceEntity.fromDomain(experience)
}
