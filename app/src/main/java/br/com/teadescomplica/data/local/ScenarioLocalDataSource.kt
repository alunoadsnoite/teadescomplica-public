package br.com.teadescomplica.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.teadescomplica.domain.model.Scenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.scenarioPreferences by preferencesDataStore(name = "tea_descomplica_scenarios")

class ScenarioLocalDataSource(private val context: Context) {

    private val dataStore = context.applicationContext.scenarioPreferences
    private val scenariosKey = stringPreferencesKey("scenarios_json")

    fun getAllScenarios(): Flow<List<Scenario>> =
        dataStore.data.map { prefs: Preferences ->
            val json = prefs[scenariosKey] ?: "[]"
            ScenarioSerializer.fromJson(json)
                .sortedByDescending { it.updatedAt }
                .sortedByDescending { it.id }
        }

    fun getScenarioById(id: Long): Flow<Scenario?> =
        getAllScenarios().map { list -> list.find { it.id == id } }

    suspend fun saveScenario(scenario: Scenario): Long {
        val now = System.currentTimeMillis()
        val toSave =
            if (scenario.id == 0L) {
                scenario.copy(createdAt = now, updatedAt = now)
            } else {
                scenario.copy(updatedAt = now)
            }
        val newId = if (toSave.id == 0L) System.currentTimeMillis() else toSave.id
        val finalScenario = toSave.copy(id = newId)

        dataStore.edit { prefs ->
            val current = prefs[scenariosKey] ?: "[]"
            val list = ScenarioSerializer.fromJson(current)
            val updated = list.filter { it.id != finalScenario.id } + finalScenario
            prefs[scenariosKey] = ScenarioSerializer.toJson(updated)
        }

        return newId
    }

    suspend fun deleteScenario(id: Long) {
        dataStore.edit { prefs ->
            val current = prefs[scenariosKey] ?: "[]"
            val list = ScenarioSerializer.fromJson(current)
            val updated = list.filter { it.id != id }
            prefs[scenariosKey] = ScenarioSerializer.toJson(updated)
        }
    }
}

object ScenarioSerializer {
    fun toJson(scenarios: List<Scenario>): String =
        scenarios.joinToString(prefix = "[", postfix = "]") { s ->
            """{"id":${s.id},"title":"${escape(s.title)}","description":"${escape(s.description)}","objective":"${escape(s.objective)}","createdAt":${s.createdAt},"updatedAt":${s.updatedAt}}"""
        }

    fun fromJson(json: String): List<Scenario> {
        val trimmed = json.trim()
        if (trimmed.isEmpty() || trimmed == "[]") return emptyList()
        val objects = mutableListOf<String>()
        var depth = 0
        var current = StringBuilder()
        for (ch in trimmed) {
            when (ch) {
                '{' -> {
                    if (depth++ == 0) current = StringBuilder() else current.append(ch)
                }
                '}' -> {
                    if (--depth == 0) objects.add(current.append('}').toString())
                    else current.append(ch)
                }
                else -> if (depth > 0) current.append(ch)
            }
        }
        return objects.map { parseScenario(it) }
    }

    private fun parseScenario(json: String): Scenario {
        fun field(name: String): String {
            val pattern = "\"$name\":\"([^\"]*)\"".toRegex()
            val match = pattern.find(json)
            return match?.groupValues?.get(1) ?: ""
        }
        fun longField(name: String): Long {
            val pattern = "\"$name\":(\\d+)".toRegex()
            val match = pattern.find(json)
            return match?.groupValues?.get(1)?.toLong() ?: 0L
        }
        return Scenario(
            id = longField("id"),
            title = unescape(field("title")),
            description = unescape(field("description")),
            objective = unescape(field("objective")),
            createdAt = longField("createdAt"),
            updatedAt = longField("updatedAt"),
        )
    }

    private fun escape(s: String): String =
        s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

    private fun unescape(s: String): String =
        s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\")
}
