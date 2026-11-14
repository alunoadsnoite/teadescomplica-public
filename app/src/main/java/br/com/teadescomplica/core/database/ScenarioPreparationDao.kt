package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ScenarioPreparationDao {

    @Query(
        "SELECT * FROM scenario_preparations WHERE scenario_id = :scenarioId ORDER BY updated_at DESC, id DESC LIMIT 1"
    )
    fun getPreparationByScenarioId(scenarioId: Long): Flow<ScenarioPreparationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreparation(preparation: ScenarioPreparationEntity): Long

    @Transaction
    suspend fun savePreparation(preparation: ScenarioPreparationEntity): Long {
        val existing = getPreparationByScenarioId(preparation.scenarioId).first()
        return if (existing == null) insertPreparation(preparation)
        else {
            updatePreparation(preparation.copy(id = existing.id))
            existing.id
        }
    }

    @Update suspend fun updatePreparation(preparation: ScenarioPreparationEntity)

    @Delete suspend fun deletePreparation(preparation: ScenarioPreparationEntity)

    @Query("DELETE FROM scenario_preparations WHERE scenario_id = :scenarioId")
    suspend fun deletePreparationByScenarioId(scenarioId: Long)

    @Query("DELETE FROM scenario_preparations") suspend fun deleteAllPreparations()

    @Query("SELECT * FROM scenario_preparations")
    fun getAllPreparations(): Flow<List<ScenarioPreparationEntity>>
}
