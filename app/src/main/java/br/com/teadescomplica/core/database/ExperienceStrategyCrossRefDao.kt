package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperienceStrategyCrossRefDao {

    @Query(
        "SELECT strategy_id FROM experience_strategy_cross_ref WHERE experience_id = :experienceId"
    )
    fun getStrategyIdsForExperience(experienceId: Long): Flow<List<Long>>

    @Query(
        "SELECT experience_id FROM experience_strategy_cross_ref WHERE strategy_id = :strategyId"
    )
    fun getExperienceIdsForStrategy(strategyId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: ExperienceStrategyCrossRefEntity)

    @Delete suspend fun deleteCrossRef(crossRef: ExperienceStrategyCrossRefEntity)

    @Query("DELETE FROM experience_strategy_cross_ref WHERE experience_id = :experienceId")
    suspend fun deleteCrossRefsForExperience(experienceId: Long)

    @Query("DELETE FROM experience_strategy_cross_ref WHERE strategy_id = :strategyId")
    suspend fun deleteCrossRefsForStrategy(strategyId: Long)

    @Query("DELETE FROM experience_strategy_cross_ref") suspend fun deleteAllCrossRefs()

    @Query("SELECT * FROM experience_strategy_cross_ref")
    fun getAllCrossRefs(): Flow<List<ExperienceStrategyCrossRefEntity>>
}
