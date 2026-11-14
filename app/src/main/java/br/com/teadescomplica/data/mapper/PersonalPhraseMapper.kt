package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.core.database.PersonalPhraseEntity
import br.com.teadescomplica.domain.model.PersonalPhrase

object PersonalPhraseMapper {
    fun toDomain(entity: PersonalPhraseEntity): PersonalPhrase = entity.toDomain()

    fun toEntity(phrase: PersonalPhrase): PersonalPhraseEntity = PersonalPhraseEntity.fromDomain(phrase)
}
