sed -i '$ a \
@Entity(tableName = "pulse_cache")\
data class PulseCacheEntity(\
    @PrimaryKey val id: String,\
    val authorName: String = "",\
    val username: String = "",\
    val userAvatar: String = "",\
    val content: String = "",\
    val mediaUrl: String = "",\
    val landmark: String = "",\
    val location: String = "",\
    val latitude: Double = 0.0,\
    val longitude: Double = 0.0,\
    val distanceKm: Double = 1.2,\
    val timestamp: Long = System.currentTimeMillis(),\
    val likesCount: Int = 0,\
    val isLiked: Boolean = false,\
    val commentsCount: Int = 0,\
    val tags: String = ""\
)' app/src/main/java/com/example/data/LocaliModels.kt
