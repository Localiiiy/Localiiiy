package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PostEntity::class,
        ReelEntity::class,
        StoryEntity::class,
        CommentEntity::class,
        UserProfileEntity::class,
        OtherUserEntity::class,
        NotificationEntity::class,
        DirectMessageEntity::class,
        ChatMessageEntity::class,
        PrivacySettingsEntity::class,
        MarketplaceItemEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun instagramDao(): InstagramDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localiiiy_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.instagramDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: InstagramDao) {
                dao.insertOrUpdateProfile(InitialData.defaultProfile)
                dao.insertStories(InitialData.starterStories)
                dao.insertPosts(InitialData.starterPosts)
                dao.insertReels(InitialData.starterReels)
                dao.insertOtherUsers(InitialData.starterOtherUsers)
                dao.insertComments(InitialData.starterComments)
                dao.insertNotifications(InitialData.starterNotifications)
                dao.insertConversations(InitialData.starterConversations)
                dao.insertChatMessages(InitialData.starterChatMessages)
                dao.insertOrUpdatePrivacySettings(InitialData.defaultPrivacySettings)
                dao.insertMarketplaceItems(InitialData.starterMarketplaceItems)
            }
        }
    }
}
