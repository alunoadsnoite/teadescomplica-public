package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.ScenarioEntity
import br.com.teadescomplica.domain.model.Scenario

object ScenarioMapper {
    fun toDomain(entity: ScenarioEntity): Scenario = entity.toDomain()

    fun toEntity(scenario: Scenario): ScenarioEntity = ScenarioEntity.fromDomain(scenario)
}
