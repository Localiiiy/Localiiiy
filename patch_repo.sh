sed -i '/val allPosts: Flow/i \
    val allPulseCache: Flow<List<PulseCacheEntity>> = localiDao.getAllPulseCache()\
\
    suspend fun refreshPulseCache(pulses: List<PulseCacheEntity>) {\
        localiDao.clearPulseCache()\
        localiDao.insertPulseCache(pulses)\
    }\
' app/src/main/java/com/example/data/LocaliRepository.kt
