package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.teadescomplica.domain.model.PossibleSituation
import kotlinx.coroutines.flow.Flow

@Dao
interface PossibleSituationDao {

    @Query(
        "SELECT * FROM possible_situations WHERE scenario_id = :scenarioId ORDER BY created_at ASC, id ASC"
    )
    fun getPossibleSituationsByScenarioId(scenarioId: Long): Flow<List<PossibleSituationEntity>>

    @Query("SELECT * FROM possible_situations WHERE id = :id LIMIT 1")
    fun getPossibleSituationById(id: Long): Flow<PossibleSituationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPossibleSituation(situation: PossibleSituationEntity): Long

    @Update
    suspend fun updatePossibleSituation(situation: PossibleSituationEntity)

    @Delete
    suspend fun deletePossibleSituation(situation: PossibleSituationEntity)

    @Query("UPDATE alternative_plans SET possible_situation_id = NULL WHERE possible_situation_id = :situationId")
    suspend fun nullifyAlternativePlanReferences(situationId: Long)

    @Query("DELETE FROM possible_situations")
    suspend fun deleteAllPossibleSituations()

    @Query("SELECT * FROM possible_situations ORDER BY created_at ASC, id ASC")
    fun getAllPossibleSituations(): Flow<List<PossibleSituationEntity>>
}
