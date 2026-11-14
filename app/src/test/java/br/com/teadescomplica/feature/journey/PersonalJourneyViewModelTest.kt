package br.com.teadescomplica.feature.journey

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
class PersonalJourneyViewModelTest : RegressionFixture() {

    @Test
    fun changingAndClearingSearchUsesFullDataset() = runBlocking {
        experienceRepo().insertExperience(PersonalExperience(title = "Consulta"))
        experienceRepo().insertExperience(PersonalExperience(title = "Entrevista"))
        val vm = keep(PersonalJourneyViewModel(experienceRepo()))
        awaitCondition { vm.uiState.value.experiences.size == 2 }
        vm.searchExperiences("Consulta")
        awaitCondition { vm.uiState.value.experiences.size == 1 }
        assertEquals("Consulta", vm.uiState.value.experiences.single().title)
        vm.searchExperiences("Entrevista")
        awaitCondition { vm.uiState.value.experiences.firstOrNull()?.title == "Entrevista" }
        vm.searchExperiences("")
        awaitCondition { vm.uiState.value.experiences.size == 2 }
    }
}
