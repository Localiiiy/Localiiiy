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
        ClipEntity::class,
        StoryEntity::class,
        CommentEntity::class,
        UserProfileEntity::class,
        OtherUserEntity::class,
        NotificationEntity::class,
        DirectMessageEntity::class,
        ChatMessageEntity::class,
        PrivacySettingsEntity::class,
        MarketplaceItemEntity::class,
        StudioVideoEntity::class,
        UserActivityEntity::class,
        SavedPostEntity::class,
        StudioDraftEntity::class,
        PulseCacheEntity::class,
        DraftClipEntity::class,
        CyberstalkingIncidentEntity::class,
        MerchantBountyEntity::class
    ],
    version = 18,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localiiiyDao(): LocaliiiyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Localiiiy_database"
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
                        populateInitialData(database.localiiiyDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: LocaliiiyDao) {
                dao.insertOrUpdateProfile(InitialData.defaultProfile)
                dao.insertStories(InitialData.starterStories)
                dao.insertPosts(InitialData.starterPosts)
                dao.insertClips(InitialData.starterClips)
                dao.insertOtherUsers(InitialData.starterOtherUsers)
                dao.insertComments(InitialData.starterComments)
                dao.insertNotifications(InitialData.starterNotifications)
                dao.insertConversations(InitialData.starterConversations)
                dao.insertChatMessages(InitialData.starterChatMessages)
                dao.insertOrUpdatePrivacySettings(InitialData.defaultPrivacySettings)
                dao.insertMarketplaceItems(InitialData.starterMarketplaceItems)
                dao.insertStudioVideos(InitialData.starterStudioVideos)
                dao.insertUserActivities(InitialData.starterUserActivities)
                dao.insertSavedPostsCache(InitialData.starterSavedPosts)
                dao.insertStudioDrafts(InitialData.starterStudioDrafts)
                dao.insertDrafts(InitialData.starterDraftClips)
                dao.insertMerchantBounties(InitialData.starterMerchantBounties)
            }
        }
    }
}
