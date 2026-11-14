package br.com.teadescomplica.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração do banco de dados da versão 2 para a versão 3.
 *
 * Adiciona as tabelas:
 * - personal_experiences (registro de experiências vividas, opcionalmente relacionadas a um
 *   cenário)
 * - personal_strategies (estratégias pessoais criadas pela pessoa)
 * - experience_strategy_cross_ref (relacionamento muitos-para-muitos entre experiências e
 *   estratégias)
 *
 * Preserva todos os dados existentes nas tabelas anteriores. Não utiliza
 * fallbackToDestructiveMigration().
 *
 * Integridade referencial:
 * - personal_experiences.scenario_id -> scenarios.id ON DELETE SET NULL (preserva a experiência
 *   quando o cenário é removido)
 * - experience_strategy_cross_ref.experience_id -> personal_experiences.id ON DELETE CASCADE
 * - experience_strategy_cross_ref.strategy_id -> personal_strategies.id ON DELETE CASCADE
 */
val Migration2To3 =
    object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Tabela de experiências pessoais
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS personal_experiences (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scenario_id INTEGER,
                title TEXT NOT NULL DEFAULT '',
                occurred_at INTEGER,
                description TEXT NOT NULL DEFAULT '',
                what_helped TEXT NOT NULL DEFAULT '',
                what_was_difficult TEXT NOT NULL DEFAULT '',
                next_time_notes TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (scenario_id) REFERENCES scenarios (id) ON DELETE SET NULL
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_experiences_scenario_id ON personal_experiences (scenario_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_experiences_occurred_at ON personal_experiences (occurred_at)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_experiences_created_at ON personal_experiences (created_at)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_experiences_title ON personal_experiences (title)"
            )

            // Tabela de estratégias pessoais
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS personal_strategies (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL DEFAULT '',
                description TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL DEFAULT 0
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_strategies_title ON personal_strategies (title)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_strategies_created_at ON personal_strategies (created_at)"
            )

            // Tabela associativa muitos-para-muitos entre experiências e estratégias
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS experience_strategy_cross_ref (
                experience_id INTEGER NOT NULL,
                strategy_id INTEGER NOT NULL,
                PRIMARY KEY (experience_id, strategy_id),
                FOREIGN KEY (experience_id) REFERENCES personal_experiences (id) ON DELETE CASCADE,
                FOREIGN KEY (strategy_id) REFERENCES personal_strategies (id) ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_experience_strategy_cross_ref_experience_id ON experience_strategy_cross_ref (experience_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_experience_strategy_cross_ref_strategy_id ON experience_strategy_cross_ref (strategy_id)"
            )
        }
    }
