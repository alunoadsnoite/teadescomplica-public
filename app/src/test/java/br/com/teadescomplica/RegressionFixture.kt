package br.com.teadescomplica

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.core.datastore.PreferenceRepository
import br.com.teadescomplica.data.repository.*
import br.com.teadescomplica.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
open class RegressionFixture {
    lateinit var db: AppDatabase
    lateinit var prefs: PreferenceRepository
    lateinit var backup: BackupRepositoryImpl
    val context: Application
        get() = ApplicationProvider.getApplicationContext()

    private val store = ViewModelStore()
    private val models = mutableListOf<ViewModel>()

    @Before
    fun createDatabase() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        db =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        prefs = PreferenceRepository(context)
        backup = BackupRepositoryImpl(context, prefs, db)
    }

    @After
    fun closeDatabase() {
        val jobs = models.mapNotNull { it.viewModelScope.coroutineContext[Job] }
        store.clear()
        runBlocking { jobs.joinAll() }
        db.close()
        Dispatchers.resetMain()
    }

    fun <T : ViewModel> keep(vm: T): T {
        models.add(vm)
        store.put(vm.javaClass.name, vm)
        return vm
    }

    suspend fun scenario(title: String = "Consulta"): Long =
        db.scenarioDao()
            .insertScenario(
                ScenarioEntity.fromDomain(
                    Scenario(
                        title = title,
                        description = "",
                        objective = "",
                        createdAt = 1,
                        updatedAt = 1,
                    )
                )
            )

    fun scenarioRepo() = ScenarioRepositoryImpl.create(db)

    fun preparationRepo() = ScenarioPreparationRepositoryImpl.create(db)

    fun situationRepo() = PossibleSituationRepositoryImpl.create(db)

    fun planRepo() = AlternativePlanRepositoryImpl.create(db)

    fun phraseRepo() = PersonalPhraseRepositoryImpl.create(db)

    fun experienceRepo() = PersonalExperienceRepositoryImpl.create(db)

    fun strategyRepo() = PersonalStrategyRepositoryImpl.create(db)

    suspend fun awaitCondition(condition: () -> Boolean) {
        withTimeout(5000) { while (!condition()) delay(20) }
    }
}
