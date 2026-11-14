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
class StrategyLibraryViewModelTest : RegressionFixture() {

    @Test
    fun searchCanBroadenAgain() = runBlocking {
        strategyRepo().insertStrategy(PersonalStrategy(title = "Respirar", description = ""))
        strategyRepo().insertStrategy(PersonalStrategy(title = "Pausa", description = ""))
        val vm = keep(StrategyLibraryViewModel(strategyRepo(), experienceRepo()))
        awaitCondition { vm.uiState.value.strategies.size == 2 }
        vm.searchStrategies("Resp")
        awaitCondition { vm.uiState.value.strategies.size == 1 }
        vm.searchStrategies("Pausa")
        awaitCondition { vm.uiState.value.strategies.firstOrNull()?.title == "Pausa" }
        vm.searchStrategies("")
        awaitCondition { vm.uiState.value.strategies.size == 2 }
    }
}
