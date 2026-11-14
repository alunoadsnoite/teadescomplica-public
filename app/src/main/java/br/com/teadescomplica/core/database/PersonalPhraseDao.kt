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
interface PersonalPhraseDao {

    @Query(
        "SELECT * FROM personal_phrases WHERE scenario_id = :scenarioId ORDER BY position ASC, created_at ASC"
    )
    fun getPersonalPhrasesByScenarioId(scenarioId: Long): Flow<List<PersonalPhraseEntity>>

    @Query("SELECT * FROM personal_phrases WHERE id = :id LIMIT 1")
    fun getPersonalPhraseById(id: Long): Flow<PersonalPhraseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalPhrase(phrase: PersonalPhraseEntity): Long

    @Transaction
    suspend fun reorder(scenarioId: Long, values: List<PersonalPhraseEntity>) {
        val current = getPersonalPhrasesByScenarioId(scenarioId).first()
        require(
            values.map { it.id }.toSet() == current.map { it.id }.toSet() &&
                values.size == current.size
        )
        values.forEachIndexed { index, value ->
            updatePersonalPhrase(value.copy(scenarioId = scenarioId, position = index))
        }
    }

    @Update suspend fun updatePersonalPhrase(phrase: PersonalPhraseEntity)

    @Delete suspend fun deletePersonalPhrase(phrase: PersonalPhraseEntity)

    @Query("DELETE FROM personal_phrases WHERE scenario_id = :scenarioId")
    suspend fun deletePersonalPhrasesByScenarioId(scenarioId: Long)

    @Query("DELETE FROM personal_phrases") suspend fun deleteAllPersonalPhrases()

    @Query("SELECT * FROM personal_phrases ORDER BY position ASC, created_at ASC")
    fun getAllPersonalPhrases(): Flow<List<PersonalPhraseEntity>>
}
