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
class PersonalExperienceRepositoryTest : RegressionFixture() {

    @Test
    fun savesAndUpdatesAssociationsAtomically() = runBlocking {
        val sr = strategyRepo()
        val repo = experienceRepo()
        val a = sr.insertStrategy(PersonalStrategy(title = "A", description = ""))
        val b = sr.insertStrategy(PersonalStrategy(title = "B", description = ""))
        val id =
            repo.saveExperienceWithStrategies(PersonalExperience(title = "Original"), setOf(a, b))
        repo.saveExperienceWithStrategies(
            repo.getExperienceById(id).first()!!.copy(title = "Editada"),
            setOf(b),
        )
        assertEquals("Editada", repo.getExperienceById(id).first()!!.title)
        assertEquals(listOf(b), repo.getStrategiesForExperience(id).first())
    }

    @Test
    fun invalidAssociationRollsBackExperience() = runBlocking {
        try {
            experienceRepo()
                .saveExperienceWithStrategies(
                    PersonalExperience(title = "Não persistir"),
                    setOf(999),
                )
            fail()
        } catch (_: android.database.sqlite.SQLiteConstraintException) {}
        assertTrue(experienceRepo().getAllExperiences().first().isEmpty())
    }

    @Test
    fun searchIncludesNotes() = runBlocking {
        experienceRepo()
            .insertExperience(PersonalExperience(title = "Consulta", nextTimeNotes = "Levar água"))
        assertEquals(1, experienceRepo().searchExperiences("água").first().size)
    }
}
