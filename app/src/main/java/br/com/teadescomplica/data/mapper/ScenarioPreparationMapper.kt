package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.ScenarioPreparationEntity
import br.com.teadescomplica.domain.model.ScenarioPreparation

object ScenarioPreparationMapper {
    fun toDomain(entity: ScenarioPreparationEntity): ScenarioPreparation = entity.toDomain()

    fun toEntity(preparation: ScenarioPreparation): ScenarioPreparationEntity =
        ScenarioPreparationEntity.fromDomain(preparation)
}
