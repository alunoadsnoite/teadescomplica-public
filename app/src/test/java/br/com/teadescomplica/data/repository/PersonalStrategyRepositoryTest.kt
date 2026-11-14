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
class PersonalStrategyRepositoryTest : RegressionFixture() {

    @Test
    fun strategyCrudAndReuse() = runBlocking {
        val r = strategyRepo()
        val id = r.insertStrategy(PersonalStrategy(title = "Pausa", description = "Respirar"))
        r.updateStrategy(r.getStrategyById(id).first()!!.copy(title = "Pausa curta"))
        val a = experienceRepo().insertExperience(PersonalExperience(title = "A"))
        val b = experienceRepo().insertExperience(PersonalExperience(title = "B"))
        r.associateStrategyToExperience(id, a)
        r.associateStrategyToExperience(id, b)
        r.associateStrategyToExperience(id, a)
        assertEquals(2, r.getExperienceIdsForStrategy(id).first().size)
        r.disassociateStrategyFromExperience(id, a)
        assertEquals(listOf(b), r.getExperienceIdsForStrategy(id).first())
        assertEquals("Pausa curta", r.getStrategyById(id).first()!!.title)
    }
}
