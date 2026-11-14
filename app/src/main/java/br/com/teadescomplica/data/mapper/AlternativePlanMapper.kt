package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.AlternativePlanEntity
import br.com.teadescomplica.domain.model.AlternativePlan

object AlternativePlanMapper {
    fun toDomain(entity: AlternativePlanEntity): AlternativePlan = entity.toDomain()

    fun toEntity(plan: AlternativePlan): AlternativePlanEntity = AlternativePlanEntity.fromDomain(plan)
}
