package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.teadescomplica.domain.model.Scenario
import kotlinx.coroutines.flow.Flow

@Dao
interface ScenarioDao {

    @Query("SELECT * FROM scenarios ORDER BY updated_at DESC, id DESC")
    fun getAllScenarios(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM scenarios WHERE id = :id LIMIT 1")
    fun getScenarioById(id: Long): Flow<ScenarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenario: ScenarioEntity): Long

    @Update
    suspend fun updateScenario(scenario: ScenarioEntity)

    @Delete
    suspend fun deleteScenario(scenario: ScenarioEntity)

    @Query("DELETE FROM scenarios")
    suspend fun deleteAllScenarios()
}
