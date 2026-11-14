package br.com.teadescomplica.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.teadescomplica.core.database.migration.Migration1To2
import br.com.teadescomplica.core.database.migration.Migration2To3

/**
 * Banco de dados Room do TEA Descomplica.
 *
 * Versão 3: adiciona tabelas de experiências pessoais, estratégias pessoais e relacionamento
 * muitos-para-muitos entre experiências e estratégias.
 *
 * A migração v2→v3 preserva os dados existentes (cenários, preparações, situações possíveis, planos
 * alternativos, frases pessoais) e cria as novas tabelas sem destruí-las.
 *
 * fallbackToDestructiveMigration está desabilitado para forçar migrações versionadas quando o
 * esquema evoluir.
 */
@Database(
    entities =
        [
            ScenarioEntity::class,
            ScenarioPreparationEntity::class,
            PossibleSituationEntity::class,
            AlternativePlanEntity::class,
            PersonalPhraseEntity::class,
            PersonalExperienceEntity::class,
            PersonalStrategyEntity::class,
            ExperienceStrategyCrossRefEntity::class,
        ],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scenarioDao(): ScenarioDao

    abstract fun scenarioPreparationDao(): ScenarioPreparationDao

    abstract fun possibleSituationDao(): PossibleSituationDao

    abstract fun alternativePlanDao(): AlternativePlanDao

    abstract fun personalPhraseDao(): PersonalPhraseDao

    abstract fun personalExperienceDao(): PersonalExperienceDao

    abstract fun personalStrategyDao(): PersonalStrategyDao

    abstract fun experienceStrategyCrossRefDao(): ExperienceStrategyCrossRefDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance =
                        Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "tea_descomplica.db",
                            )
                            .addMigrations(Migration1To2, Migration2To3)
                            .build()
                    INSTANCE = instance
                    instance
                }
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }
}
