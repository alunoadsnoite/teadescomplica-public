package br.com.teadescomplica.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração do banco de dados da versão 1 para a versão 2.
 *
 * Adiciona as tabelas:
 * - scenario_preparations (1:1 com scenarios)
 * - possible_situations (1:N com scenarios)
 * - alternative_plans (1:N com scenarios, 1:N opcional com possible_situations via SET NULL)
 * - personal_phrases (1:N com scenarios)
 *
 * Preserva todos os dados existentes na tabela scenarios. Não utiliza
 * fallbackToDestructiveMigration().
 */
val Migration1To2 =
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Tabela de preparações (um-para-um com cenário)
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS scenario_preparations (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scenario_id INTEGER NOT NULL,
                location TEXT NOT NULL DEFAULT '',
                scheduled_at INTEGER,
                what_to_expect TEXT NOT NULL DEFAULT '',
                what_to_bring TEXT NOT NULL DEFAULT '',
                important_information TEXT NOT NULL DEFAULT '',
                personal_notes TEXT NOT NULL DEFAULT '',
                updated_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (scenario_id) REFERENCES scenarios (id) ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_scenario_preparations_scenario_id ON scenario_preparations (scenario_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_scenario_preparations_updated_at ON scenario_preparations (updated_at)"
            )

            // Tabela de situações possíveis (um-para-muitos com cenário)
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS possible_situations (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scenario_id INTEGER NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                likelihood TEXT,
                created_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (scenario_id) REFERENCES scenarios (id) ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_possible_situations_scenario_id ON possible_situations (scenario_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_possible_situations_created_at ON possible_situations (created_at)"
            )

            // Tabela de planos alternativos (um-para-muitos com cenário, SET NULL com situação)
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS alternative_plans (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scenario_id INTEGER NOT NULL,
                possible_situation_id INTEGER,
                title TEXT NOT NULL DEFAULT '',
                description TEXT NOT NULL DEFAULT '',
                position INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (scenario_id) REFERENCES scenarios (id) ON DELETE CASCADE,
                FOREIGN KEY (possible_situation_id) REFERENCES possible_situations (id) ON DELETE SET NULL
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_alternative_plans_scenario_id ON alternative_plans (scenario_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_alternative_plans_possible_situation_id ON alternative_plans (possible_situation_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_alternative_plans_position ON alternative_plans (position)"
            )

            // Tabela de frases pessoais (um-para-muitos com cenário)
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS personal_phrases (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scenario_id INTEGER NOT NULL,
                text TEXT NOT NULL DEFAULT '',
                position INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (scenario_id) REFERENCES scenarios (id) ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_phrases_scenario_id ON personal_phrases (scenario_id)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_personal_phrases_position ON personal_phrases (position)"
            )
        }
    }
