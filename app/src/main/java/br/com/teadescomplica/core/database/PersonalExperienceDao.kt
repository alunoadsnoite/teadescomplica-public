package br.com.teadescomplica.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalExperienceDao {

    @Query(
        "SELECT * FROM personal_experiences ORDER BY " +
            "CASE WHEN occurred_at IS NULL THEN 1 ELSE 0 END, " +
            "occurred_at DESC, " +
            "created_at DESC"
    )
    fun getAllExperiences(): Flow<List<PersonalExperienceEntity>>

    @Query("SELECT * FROM personal_experiences WHERE id = :id LIMIT 1")
    fun getExperienceById(id: Long): Flow<PersonalExperienceEntity?>

    @Query(
        "SELECT * FROM personal_experiences WHERE scenario_id = :scenarioId ORDER BY occurred_at DESC, created_at DESC"
    )
    fun getExperiencesByScenarioId(scenarioId: Long): Flow<List<PersonalExperienceEntity>>

    @Query(
        "SELECT * FROM personal_experiences " +
            "WHERE title LIKE :query OR description LIKE :query OR what_helped LIKE :query " +
            "OR what_was_difficult LIKE :query OR next_time_notes LIKE :query " +
            "ORDER BY created_at DESC"
    )
    fun searchExperiences(query: String): Flow<List<PersonalExperienceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperience(experience: PersonalExperienceEntity): Long

    @Update suspend fun updateExperience(experience: PersonalExperienceEntity)

    @Delete suspend fun deleteExperience(experience: PersonalExperienceEntity)

    @Query("DELETE FROM personal_experiences WHERE id = :id")
    suspend fun deleteExperienceById(id: Long)

    @Query("DELETE FROM personal_experiences") suspend fun deleteAllPersonalExperiences()
}
