package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.PossibleSituationEntity
import br.com.teadescomplica.domain.model.PossibleSituation

object PossibleSituationMapper {
    fun toDomain(entity: PossibleSituationEntity): PossibleSituation = entity.toDomain()

    fun toEntity(situation: PossibleSituation): PossibleSituationEntity =
        PossibleSituationEntity.fromDomain(situation)
}
