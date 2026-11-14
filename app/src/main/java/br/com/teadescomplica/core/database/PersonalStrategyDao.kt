package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.teadescomplica.domain.model.PersonalStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalStrategyDao {

    @Query("SELECT * FROM personal_strategies ORDER BY title ASC")
    fun getAllStrategies(): Flow<List<PersonalStrategyEntity>>

    @Query("SELECT * FROM personal_strategies WHERE id = :id LIMIT 1")
    fun getStrategyById(id: Long): Flow<PersonalStrategyEntity?>

    @Query(
        "SELECT * FROM personal_strategies " +
            "WHERE title LIKE :query OR description LIKE :query " +
            "ORDER BY title ASC"
    )
    fun searchStrategies(query: String): Flow<List<PersonalStrategyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrategy(strategy: PersonalStrategyEntity): Long

    @Update
    suspend fun updateStrategy(strategy: PersonalStrategyEntity)

    @Delete
    suspend fun deleteStrategy(strategy: PersonalStrategyEntity)

    @Query("DELETE FROM personal_strategies WHERE id = :id")
    suspend fun deleteStrategyById(id: Long)

    @Query("DELETE FROM personal_strategies")
    suspend fun deleteAllPersonalStrategies()
}
