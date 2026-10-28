package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "aod_presets")
data class AodPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val badgeTag: String,
    val clockStyleId: String,
    val edgeStyleId: String,
    val paletteId: String,
    val ringModeId: String,
    val weatherStyleId: String,
    val islandShapeId: String = "split_drop",
    val burnInCheckerboard: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_color_rules")
data class AppColorRuleEntity(
    @PrimaryKey val packageName: String,
    val appDisplayName: String,
    val categoryRu: String,
    val colorHex: Long,
    val ringPulseEnabled: Boolean = true,
    val edgeWaveEnabled: Boolean = true,
    val showInDynamicIsland: Boolean = true,
    val isWhitelisted: Boolean = true,
    val isBlacklisted: Boolean = false,
    val wakeScreenOnPush: Boolean = true,
    val autoExpandIslandOnPush: Boolean = true,
    val priorityLevel: Int = 2 // 1 = Low, 2 = Normal, 3 = VIP High
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String = "",
    val senderOrTitle: String,
    val messageBody: String,
    val colorHex: Long,
    val wasBlockedByFilter: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface LuminaDao {
    @Query("SELECT * FROM aod_presets ORDER BY createdAt DESC")
    fun observeAllPresets(): Flow<List<AodPresetEntity>>

    @Query("SELECT COUNT(*) FROM aod_presets")
    suspend fun getPresetCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: AodPresetEntity)

    @Query("DELETE FROM aod_presets WHERE id = :id")
    suspend fun deletePresetById(id: Int)

    @Query("SELECT * FROM app_color_rules ORDER BY priorityLevel DESC, appDisplayName ASC")
    fun observeAppColorRules(): Flow<List<AppColorRuleEntity>>

    @Query("SELECT * FROM app_color_rules")
    suspend fun getAllAppColorRulesOnce(): List<AppColorRuleEntity>

    @Query("SELECT COUNT(*) FROM app_color_rules")
    suspend fun getAppRuleCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppColorRule(rule: AppColorRuleEntity)

    @Query("DELETE FROM app_color_rules WHERE packageName = :packageName")
    suspend fun deleteAppRuleByPackage(packageName: String)

    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC LIMIT 30")
    fun observeRecentNotifications(): Flow<List<NotificationLogEntity>>

    @Query("SELECT COUNT(*) FROM notification_logs")
    suspend fun getNotificationLogCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLog(log: NotificationLogEntity)

    @Query("DELETE FROM notification_logs")
    suspend fun clearNotificationLogs()
}

@Database(
    entities = [
        AodPresetEntity::class,
        AppColorRuleEntity::class,
        NotificationLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun luminaDao(): LuminaDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

        fun getInstance(context: Context): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_aod_studio.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
