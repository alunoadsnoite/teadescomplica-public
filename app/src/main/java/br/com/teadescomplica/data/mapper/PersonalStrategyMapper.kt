package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.PersonalStrategyEntity
import br.com.teadescomplica.domain.model.PersonalStrategy

object PersonalStrategyMapper {
    fun toDomain(entity: PersonalStrategyEntity): PersonalStrategy = entity.toDomain()

    fun toEntity(strategy: PersonalStrategy): PersonalStrategyEntity =
        PersonalStrategyEntity.fromDomain(strategy)
}
