package br.com.teadescomplica.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.*
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.core.database.migration.*
import br.com.teadescomplica.core.datastore.PreferenceRepository
import br.com.teadescomplica.data.repository.BackupRepositoryImpl
import br.com.teadescomplica.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseRegressionTest {
    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun migrationFromVersionOnePreservesScenario() = runBlocking {
        val name = "migration-test-${System.nanoTime()}.db"
        try {
            val helper =
                FrameworkSQLiteOpenHelperFactory()
                    .create(
                        SupportSQLiteOpenHelper.Configuration.builder(context)
                            .name(name)
                            .callback(
                                object : SupportSQLiteOpenHelper.Callback(1) {
                                    override fun onCreate(db: SupportSQLiteDatabase) {
                                        db.execSQL(
                                            "CREATE TABLE scenarios (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, objective TEXT NOT NULL, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)"
                                        )
                                        db.execSQL(
                                            "INSERT INTO scenarios VALUES (7,'Preservado','','',1,2)"
                                        )
                                    }

                                    override fun onUpgrade(
                                        db: SupportSQLiteDatabase,
                                        old: Int,
                                        new: Int,
                                    ) {}
                                }
                            )
                            .build()
                    )
            helper.writableDatabase
            helper.close()
            val db =
                Room.databaseBuilder(context, AppDatabase::class.java, name)
                    .addMigrations(Migration1To2, Migration2To3)
                    .build()
            try {
                assertEquals("Preservado", db.scenarioDao().getScenarioById(7).first()!!.title)
                assertEquals(3, db.openHelper.writableDatabase.version)
            } finally {
                db.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    @Test
    fun encryptedFileExportImportPreservesDataAndRejectsWrongPassword() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val file = java.io.File(context.cacheDir, "backup-test-${System.nanoTime()}.teadbackup")
        try {
            db.scenarioDao()
                .insertScenario(
                    ScenarioEntity.fromDomain(
                        Scenario(
                            id = 5,
                            title = "Consulta",
                            description = "ç 😀",
                            objective = "Preparar",
                            createdAt = 1,
                            updatedAt = 2,
                        )
                    )
                )
            val repo = BackupRepositoryImpl(context, PreferenceRepository(context), db)
            val snapshot = repo.createSnapshot()
            val uri = android.net.Uri.fromFile(file)
            assertTrue(repo.exportEncryptedBackup(snapshot, "senhaSegura123", uri))
            assertEquals(snapshot, repo.importEncryptedBackup(uri, "senhaSegura123"))
            assertNull(repo.importEncryptedBackup(uri, "senhaIncorreta"))
            val bytes = file.readBytes()
            bytes[bytes.lastIndex] = (bytes.last().toInt() xor 1).toByte()
            file.writeBytes(bytes)
            assertNull(repo.importEncryptedBackup(uri, "senhaSegura123"))
            assertEquals("Consulta", db.scenarioDao().getScenarioById(5).first()!!.title)
        } finally {
            db.close()
            file.delete()
        }
    }
}
