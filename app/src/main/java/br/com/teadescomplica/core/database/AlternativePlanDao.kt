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
interface AlternativePlanDao {

    @Query(
        "SELECT * FROM alternative_plans WHERE scenario_id = :scenarioId ORDER BY position ASC, created_at ASC"
    )
    fun getAlternativePlansByScenarioId(scenarioId: Long): Flow<List<AlternativePlanEntity>>

    @Query("SELECT * FROM alternative_plans WHERE id = :id LIMIT 1")
    fun getAlternativePlanById(id: Long): Flow<AlternativePlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternativePlan(plan: AlternativePlanEntity): Long

    @Transaction
    suspend fun reorder(scenarioId: Long, values: List<AlternativePlanEntity>) {
        val current = getAlternativePlansByScenarioId(scenarioId).first()
        require(
            values.map { it.id }.toSet() == current.map { it.id }.toSet() &&
                values.size == current.size
        )
        values.forEachIndexed { index, value ->
            updateAlternativePlan(value.copy(scenarioId = scenarioId, position = index))
        }
    }

    @Update suspend fun updateAlternativePlan(plan: AlternativePlanEntity)

    @Delete suspend fun deleteAlternativePlan(plan: AlternativePlanEntity)

    @Query("DELETE FROM alternative_plans WHERE scenario_id = :scenarioId")
    suspend fun deleteAlternativePlansByScenarioId(scenarioId: Long)

    @Query("DELETE FROM alternative_plans") suspend fun deleteAllAlternativePlans()

    @Query("SELECT * FROM alternative_plans ORDER BY position ASC, created_at ASC")
    fun getAllAlternativePlans(): Flow<List<AlternativePlanEntity>>
}
