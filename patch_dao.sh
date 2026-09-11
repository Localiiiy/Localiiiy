sed -i '/fun getAllPosts()/i \
    @Query("SELECT * FROM pulse_cache ORDER BY timestamp DESC")\
    fun getAllPulseCache(): Flow<List<PulseCacheEntity>>\
\
    @Insert(onConflict = OnConflictStrategy.REPLACE)\
    suspend fun insertPulseCache(pulses: List<PulseCacheEntity>)\
\
    @Query("DELETE FROM pulse_cache")\
    suspend fun clearPulseCache()\
' app/src/main/java/com/example/data/LocaliDao.kt
