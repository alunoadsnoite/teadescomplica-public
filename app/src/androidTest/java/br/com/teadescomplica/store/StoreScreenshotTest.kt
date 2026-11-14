package br.com.teadescomplica.store

import android.app.LocaleManager
import android.os.LocaleList
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import br.com.teadescomplica.MainActivity
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.core.datastore.ThemePreference
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

/** Screenshots use a separate in-memory database; personal records are never seeded or cleared. */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion=33)
class StoreScreenshotTest {
    @get:Rule val ui=createAndroidComposeRule<MainActivity>()
    private lateinit var demo:AppDatabase
    private lateinit var originalLocales:LocaleList
    private lateinit var originalTheme:ThemePreference
    @Before fun isolateDemoData()=runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        originalLocales=context.getSystemService(LocaleManager::class.java).applicationLocales
        originalTheme=DiModule.getPreferenceRepository().getThemePreferenceOnce()
        demo=Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).build()
        DiModule.init(context,demo)
        DiModule.getPreferenceRepository().setThemePreference(ThemePreference.LIGHT)
        ui.activityRule.scenario.recreate()
        Unit
    }
    @After fun restorePersonalDatabase()=runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        ui.activityRule.scenario.close()
        DiModule.init(context)
        DiModule.getPreferenceRepository().setThemePreference(originalTheme)
        context.getSystemService(LocaleManager::class.java).applicationLocales=originalLocales
        demo.close()
    }
    private fun awaitText(text:String){ui.waitUntil(8000){ui.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()}}
    private fun shot(locale:String,name:String){
        ui.waitForIdle()
        android.os.SystemClock.sleep(450) // Let navigation transitions finish before the screen capture.
        val bitmap=InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val root=File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),"store-screenshots/$locale").apply{mkdirs()}
        File(root,"$name.png").outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
        bitmap.recycle()
    }
    private suspend fun seed(en:Boolean){
        demo.clearAllTables() // Only this test's isolated in-memory database.
        val now=1793448000000L
        demo.scenarioDao().insertScenario(ScenarioEntity.fromDomain(Scenario(1,if(en)"Medical appointment" else "Consulta médica",if(en)"A routine check-up at the health center." else "Uma consulta de rotina no centro de saúde.",if(en)"Explain my questions at my own pace." else "Explicar minhas dúvidas no meu ritmo.",now,now)))
        demo.scenarioDao().insertScenario(ScenarioEntity.fromDomain(Scenario(2,if(en)"Job interview" else "Entrevista de emprego",if(en)"Prepare for a conversation about a new job." else "Preparar uma conversa sobre uma nova vaga.",if(en)"Share my experience clearly." else "Apresentar minha experiência com clareza.",now,now-1)))
        demo.scenarioPreparationDao().savePreparation(ScenarioPreparationEntity.fromDomain(ScenarioPreparation(1,if(en)"Community health center" else "Centro de saúde do bairro",null,if(en)"Reception, a short wait, and a conversation with the doctor." else "Recepção, espera e conversa com a médica.",if(en)"ID, previous results, and my written questions." else "Documento, exames anteriores e minhas perguntas por escrito.",if(en)"I can ask for a quieter place to wait." else "Posso pedir um lugar mais tranquilo para esperar.",if(en)"Take one step at a time." else "Fazer uma coisa de cada vez.",now)))
        demo.possibleSituationDao().insertPossibleSituation(PossibleSituationEntity.fromDomain(PossibleSituation(1,1,if(en)"The appointment may run late." else "O atendimento pode atrasar.",SituationLikelihood.POSSIBLE,now)))
        demo.alternativePlanDao().insertAlternativePlan(AlternativePlanEntity.fromDomain(AlternativePlan(id=1,scenarioId=1,possibleSituationId=1,title=if(en)"Ask about the waiting time" else "Perguntar sobre a espera",description=if(en)"Ask reception for an estimate and take a short break." else "Pedir uma previsão na recepção e fazer uma pausa.",position=0,createdAt=now,updatedAt=now)))
        demo.personalPhraseDao().insertPersonalPhrase(PersonalPhraseEntity.fromDomain(PersonalPhrase(id=1,scenarioId=1,text=if(en)"I need a little more time to answer." else "Preciso de mais tempo para responder.",position=0,createdAt=now)))
        demo.personalStrategyDao().insertStrategy(PersonalStrategyEntity.fromDomain(PersonalStrategy(1,if(en)"Write down my questions" else "Levar perguntas por escrito",if(en)"Make a short list before leaving home. Read it at my own pace during the appointment." else "Preparar uma lista curta antes de sair. Ler com calma durante a consulta.",now,now)))
        demo.personalExperienceDao().insertExperience(PersonalExperienceEntity.fromDomain(PersonalExperience(id=1,scenarioId=1,title=if(en)"A calmer appointment" else "Uma consulta mais tranquila",description=if(en)"I asked my questions and took a break when needed." else "Consegui fazer minhas perguntas e pedir uma pausa.",whatHelped=if(en)"Having my notes with me." else "Ter minhas anotações por perto.",nextTimeNotes=if(en)"Bring the same list next time." else "Levar a mesma lista na próxima vez.",createdAt=now,updatedAt=now)))
        demo.experienceStrategyCrossRefDao().insertCrossRef(ExperienceStrategyCrossRefEntity(1,1))
    }
    @Test fun capturePortugueseAndEnglish()=runBlocking {
        for(locale in listOf("pt-BR","en-US")){
            val en=locale=="en-US"
            seed(en)
            ui.activityRule.scenario.onActivity{it.getSystemService(LocaleManager::class.java).applicationLocales=LocaleList.forLanguageTags(locale)}
            ui.waitUntil(8000){ui.activity.resources.configuration.locales[0].language==if(en)"en" else "pt"}
            val home=if(en)"Home" else "Início"
            awaitText(home);ui.onAllNodesWithText(home).onLast().performClick()
            awaitText(if(en)"Prepare in your own way." else "Prepare-se do seu jeito.")
            shot(locale,"1-home")
            ui.onAllNodesWithText(if(en)"My scenarios" else "Meus cenários").onLast().performClick()
            val title=if(en)"Medical appointment" else "Consulta médica"
            awaitText(title);shot(locale,"2-scenarios")
            ui.onNodeWithText(title).performClick()
            awaitText(if(en)"Edit preparation" else "Editar preparação");shot(locale,"3-preparation")
            ui.onAllNodesWithText(if(en)"My journey" else "Minha jornada").onLast().performClick()
            awaitText(if(en)"A calmer appointment" else "Uma consulta mais tranquila");shot(locale,"4-journey")
            ui.onNodeWithText(if(en)"Strategies" else "Estratégias").performClick()
            val strategy=if(en)"Write down my questions" else "Levar perguntas por escrito"
            awaitText(strategy);ui.onNodeWithText(strategy).performClick()
            awaitText(if(en)"Edit strategy" else "Editar estratégia");shot(locale,"5-strategy")
            ui.onAllNodesWithText(if(en)"Settings" else "Configurações").onLast().performClick()
            ui.onNodeWithText(if(en)"Manage backup" else "Gerenciar backup").performScrollTo().performClick()
            awaitText(if(en)"Create backup" else "Criar backup");shot(locale,"6-backup")
        }
    }
}
