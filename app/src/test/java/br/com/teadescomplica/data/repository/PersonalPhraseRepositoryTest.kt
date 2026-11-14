package br.com.teadescomplica.data.repository

import android.app.Application
import br.com.teadescomplica.RegressionFixture
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.data.crypto.*
import br.com.teadescomplica.data.mapper.*
import br.com.teadescomplica.data.repository.*
import br.com.teadescomplica.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class PersonalPhraseRepositoryTest : RegressionFixture() {

    @Test
    fun phraseCrudAndReorder() = runBlocking {
        val id = scenario()
        val repo = phraseRepo()
        repo.insertPersonalPhrase(PersonalPhrase(scenarioId = id, text = "A", position = 0))
        repo.insertPersonalPhrase(PersonalPhrase(scenarioId = id, text = "B", position = 1))
        repo.reorderPersonalPhrases(id, repo.getPersonalPhrasesByScenarioId(id).first().reversed())
        val ordered = repo.getPersonalPhrasesByScenarioId(id).first()
        assertEquals(listOf("B", "A"), ordered.map { it.text })
        repo.updatePersonalPhrase(ordered.first().copy(text = "Atualizada"))
        assertEquals("Atualizada", repo.getPersonalPhrasesByScenarioId(id).first().first().text)
        repo.deletePersonalPhrase(ordered.first().id)
        assertEquals(1, repo.getPersonalPhrasesByScenarioId(id).first().size)
    }
}
